package io.github.twilightflower.fumo.core.impl.transformer.jar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import io.github.twilightflower.fumo.core.impl.util.Util;

public class TransformingNioClassLoader extends SecureClassLoader {
	private final Path[] roots;
	
	public TransformingNioClassLoader(Path[] roots, ClassLoader parent) {
		super(parent);
		this.roots = roots;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String classFileName = name.replace('.', '/') + ".class";
		for(Path p : roots) {
			Path classFilePath = p.resolve(classFileName);
			if(Files.exists(classFilePath)) {
				
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
}
