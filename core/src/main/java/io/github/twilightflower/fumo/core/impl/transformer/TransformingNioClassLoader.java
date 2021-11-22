package io.github.twilightflower.fumo.core.impl.transformer;

import java.io.BufferedInputStream;
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
import io.github.twilightflower.fumo.core.impl.util.Util;

public class TransformingNioClassLoader extends SecureClassLoader {
	private final List<Path> roots = new ArrayList<>();
	private final InternalClassTransformer transformer;
	
	public TransformingNioClassLoader(Collection<Path> roots, ClassLoader parent, InternalClassTransformer transformer) {
		super(parent);
		this.roots.addAll(roots);
		this.transformer = transformer;
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
		String classFileName = slashName + ".class";
		for(Path p : roots) {
			Path classFilePath = p.resolve(classFileName);
			if(Files.exists(classFilePath)) {
				try(InputStream in = new BufferedInputStream(Files.newInputStream(classFilePath))) {
					byte[] clazz;
					if(transformer.transforms(name.replace('.', '/'))) {
						clazz = transformer.transform(name, in);
					} else {
						clazz = Util.readStream(in);
					}
					return defineClass(name, clazz, 0, clazz.length);
				} catch (IOException e) {
					throw new ClassNotFoundException(name, e);
				}
			}
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
		InputStream str = super.getResourceAsStream(loc);
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
		public boolean transforms(String className) {
			return false;
		}

		@Override
		public byte[] transform(String className, InputStream clazz) throws IOException {
			throw new UnsupportedOperationException();
		}
	}
}
