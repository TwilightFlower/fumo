/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.core.impl.transformer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import io.github.twilightflower.fumo.core.api.transformer.ClassTransformer;
import io.github.twilightflower.fumo.core.api.transformer.TransformerContext;

public class TransformCache {
	private final Map<String, ClassNode[]> transformCache = new HashMap<>();
	private final List<ClassTransformer> transformers;
	private final int transformerCount;
	private final Function<String, byte[]> classProvider;
	private final Set<String> frames = new HashSet<>();
	private final Set<String> maxs = new HashSet<>();
	
	public TransformCache(List<ClassTransformer> transformers, Function<String, byte[]> classProvider) {
		this.transformers = transformers;
		for(int i = 0; i < transformers.size(); i++) {
			transformers.get(i).acceptTransformerContext(new Context(i));
		}
		transformerCount = transformers.size();
		this.classProvider = classProvider;
	}
	
	public ClassNode getTransformedClass(String className) throws ClassNotFoundException {
		return upTo(className, transformerCount);
	}
	
	public ClassNode maybeGetTransformedClass(String className) {
		try {
			return getTransformedClass(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	public int getWriterFlags(String className) {
		int flags = 0;
		if(frames.contains(className)) {
			flags |= ClassWriter.COMPUTE_FRAMES;
		}
		if(maxs.contains(className)) {
			flags |= ClassWriter.COMPUTE_MAXS;
		}
		
		return flags;
	}
	
	/**
	 * Index 0 = no transforms.
	 * Index n = all transforms (where n is transformers.length())
	 */
	ClassNode upTo(String className, int index) throws ClassNotFoundException {
		ClassNode[] cached = transformCache.computeIfAbsent(className, str -> new ClassNode[transformerCount + 1]);
		if(cached[index] == null) {
			for(int i = 0; i <= index; i++) {
				fillPos(className, cached, i);
			}
		}
		return cached[index];
	}
	
	private void fillPos(String className, ClassNode[] cached, int index) throws ClassNotFoundException {
		if(cached[index] == null) {
			if(index == 0) {
				ClassNode node = new ClassNode();
				new ClassReader(classProvider.apply(className)).accept(node, 0);
				cached[index] = node;
			} else {
				ClassTransformer transformer = transformers.get(index - 1);
				String dottedName = className.replace('/', '.');
				if(transformer.transforms(dottedName)) {
					ClassNode copy = new ClassNode();
					cached[index - 1].accept(copy);
					cached[index] = transformer.transform(dottedName, copy); 
				} else {
					cached[index] = cached[index - 1];
				}
			}
		}
	}
	
	private class Context implements TransformerContext {
		Context(int index) {
			this.index = index;
		}
		
		final int index;
		@Override
		public ClassNode getOtherClass(String className) throws ClassNotFoundException{
			return upTo(className, index);
		}
		@Override
		public ClassNode getUntransformedClass(String className) throws ClassNotFoundException {
			return upTo(className, 0);
		}
		@Override
		public void computeMaxs(String className) {
			maxs.add(className);
		}
		@Override
		public void computeFrames(String className) {
			frames.add(className);
		}
	}
}
