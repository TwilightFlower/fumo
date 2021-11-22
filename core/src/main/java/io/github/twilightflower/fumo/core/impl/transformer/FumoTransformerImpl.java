package io.github.twilightflower.fumo.core.impl.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import io.github.twilightflower.fumo.core.api.transformer.ClassTransformer;

public class FumoTransformerImpl implements InternalClassTransformer {
	private final List<ClassTransformer> transformers = new ArrayList<>();
	
	public FumoTransformerImpl(List<ClassTransformer> transformers) {
		this.transformers.addAll(transformers);
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
	public byte[] transform(String className, InputStream clazz) throws IOException {
		ClassNode classNode = new ClassNode();
		ClassReader reader = new ClassReader(clazz);
		reader.accept(classNode, 0);
		classNode = transform(className, classNode);
		ClassWriter cw = new ClassWriter(0);
		classNode.accept(cw);
		return cw.toByteArray();
	}
	
	public ClassNode transform(String className, ClassNode classNode) {
		for(ClassTransformer t : transformers) {
			if(t.transforms(className)) {
				classNode = t.transform(className, classNode);
			}
		}
		return classNode;
	}
	
	public ClassNode transformUntil(String className, ClassNode classNode, ClassTransformer transformer) {
		for(ClassTransformer t : transformers) {
			if(Objects.equals(t, transformer)) {
				break;
			}
			if(t.transforms(className)) {
				classNode = t.transform(className, classNode);
			}
		}
		return classNode;
	}
}
