package io.github.twilightflower.fumo.core.impl.transformer;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import io.github.twilightflower.fumo.core.impl.transformer.jar.InternalClassTransformer;

public class FumoTransformerImpl implements InternalClassTransformer {
	
	
	@Override
	public boolean transforms(String className) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] transform(String className, InputStream clazz) throws IOException {
		ClassNode classNode = new ClassNode();
		ClassReader reader = new ClassReader(clazz);
		reader.accept(classNode, 0);
		
	}
}
