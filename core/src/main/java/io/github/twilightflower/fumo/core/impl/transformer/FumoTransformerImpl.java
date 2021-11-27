package io.github.twilightflower.fumo.core.impl.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import io.github.twilightflower.fumo.core.api.transformer.ClassTransformer;
import io.github.twilightflower.fumo.core.impl.util.Util;

public class FumoTransformerImpl implements InternalClassTransformer {
	private final List<ClassTransformer> transformers = new ArrayList<>();
	private TransformCache cache;
	private Map<String, byte[]> writtenCache = new HashMap<>();
	private final ClassLoader parentLoader;
	
	public FumoTransformerImpl(List<ClassTransformer> transformers, ClassLoader parentLoader) {
		this.transformers.addAll(transformers);
		this.parentLoader = parentLoader;
	}
	
	@Override
	public boolean transforms(String className) {
		for(ClassTransformer t : transformers) {
			if(t.transforms(className)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public byte[] getTransformed(String className) throws ClassNotFoundException {
		return writtenCache.computeIfAbsent(className, Util.makeSneakyFunction(name -> {
			ClassNode node = transform(name);
			ClassWriter writer = new FumoClassWriter(cache, parentLoader, cache.getWriterFlags(className));
			node.accept(writer);
			return writer.toByteArray();
		}));
	}
	
	public ClassNode transform(String className) throws ClassNotFoundException {
		return cache.getTransformedClass(className);
	}
	
	@Override
	public void acceptClassGetter(Function<String, byte[]> getter) {
		cache = new TransformCache(transformers, getter);
	}
	
	public ClassNode transformUntil(String className, ClassTransformer transformer) throws ClassNotFoundException {
		return cache.upTo(className, transformer == null ? 0 : transformers.indexOf(transformer));
	}
}
