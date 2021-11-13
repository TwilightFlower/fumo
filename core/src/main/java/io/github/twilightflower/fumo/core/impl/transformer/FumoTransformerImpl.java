package io.github.twilightflower.fumo.core.impl.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import io.github.twilightflower.fumo.core.api.transformer.ClassTransformer;
import io.github.twilightflower.fumo.core.impl.transformer.jar.InternalClassTransformer;

public class FumoTransformerImpl implements InternalClassTransformer {
	private final List<ClassTransformer> transformers = new ArrayList<>();
	
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
		for(ClassTransformer t : transformers) {
			if(t.transforms(className)) {
				classNode = t.transform(className, classNode);
			}
		}
		ClassWriter cw = new ClassWriter(0);
		classNode.accept(cw);
		return cw.toByteArray();
	}
}
