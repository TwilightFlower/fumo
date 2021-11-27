/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.core.impl.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

import io.github.twilightflower.fumo.core.impl.util.Util;

public class TransformingNioClassLoader extends SecureClassLoader {
	private final List<Path> roots = new ArrayList<>();
	private final InternalClassTransformer transformer;
	
	public TransformingNioClassLoader(Collection<Path> roots, ClassLoader parent, InternalClassTransformer transformer) {
		super(parent);
		this.roots.addAll(roots);
		this.transformer = transformer;
		transformer.acceptClassGetter(Util.makeSneakyFunction(this::getClassBytes));
	}
	
	public TransformingNioClassLoader(Collection<Path> roots, ClassLoader parent) {
		this(roots, parent, new NoopTransformer());
	}
	
	public void addRoot(Path p) {
		if(!roots.contains(p)) {
			roots.add(p);
		}
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String slashName = name.replace('.', '/');
		byte[] clazz;
		if(transformer.transforms(slashName)) {
			clazz = transformer.getTransformed(slashName);
		} else {
			clazz = getClassBytes(slashName);
		}
		return defineClass(name, clazz, 0, clazz.length);
	}
	
	private byte[] getClassBytes(String name) throws ClassNotFoundException {
		String classFileName = name + ".class";
		try(InputStream in = getResourceAsStream(classFileName)) {
			if(in != null) {
				return Util.readStream(in);
			}
		} catch(IOException e) {
			throw new ClassNotFoundException(name, e);
		}
		throw new ClassNotFoundException(name);
	}
	
	@Override
	protected URL findResource(String loc) {
		for(Path p : roots) {
			Path resPath = p.resolve(loc);
			if(Files.exists(resPath)) {
				return Util.uriToURL(resPath.toUri());
			}
		}
		return null;
	}
	
	@Override
	protected Enumeration<URL> findResources(String loc) {
		List<URL> urls = new ArrayList<>();
		for(Path p : roots) {
			Path resPath = p.resolve(loc);
			if(Files.exists(resPath)) {
				urls.add(Util.uriToURL(resPath.toUri()));
			}
		}
		return null;
	}
	
	@Override
	public InputStream getResourceAsStream(String loc) {
		InputStream str = null;
		if(getParent() != null) {
			str = getParent().getResourceAsStream(loc);
		}
		if(str == null) {
			for(Path p : roots) {
				Path resPath = p.resolve(loc);
				if(Files.exists(resPath)) {
					try {
						return Files.newInputStream(resPath);
					} catch (IOException e) {
						// continue.
					}
				}
			}
		}
		return str;
	}
	
	private static class NoopTransformer implements InternalClassTransformer {
		@Override
		public void acceptClassGetter(Function<String, byte[]> resGetter) { }
		@Override
		public boolean transforms(String className) {
			return false;
		}
		@Override
		public byte[] getTransformed(String className) {
			return null;
		}
	}
}
