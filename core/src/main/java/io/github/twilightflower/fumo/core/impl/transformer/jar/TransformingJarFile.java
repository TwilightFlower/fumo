package io.github.twilightflower.fumo.core.impl.transformer.jar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import io.github.twilightflower.fumo.core.impl.util.Util;

public class TransformingJarFile extends JarFile {
	private final Map<String, byte[]> transformed = new ConcurrentHashMap<>();
	
	private final InternalClassTransformer transformer;
	public TransformingJarFile(File file, InternalClassTransformer transformer) throws IOException {
		super(file);
		this.transformer = transformer;
	}
	
	@Override
	public ZipEntry getEntry(String name) {
		return new TransformingJarEntry(super.getJarEntry(name));
	}
	
	@Override
	public InputStream getInputStream(ZipEntry entry) throws IOException {
		String name = entry.getName();
		
		byte[] trans = transformed.get(name);
		if(trans != null) {
			return new ByteArrayInputStream(trans);
		}
		
		InputStream inStream = super.getInputStream(entry);
		if(name.endsWith(".class")) {
			String className = name.substring(0, name.length() - 6);
			if(transformer.transforms(className)) {
				return new ByteArrayInputStream(transformed.computeIfAbsent(name, Util.makeSneakyFunction(n -> transformer.transform(className, inStream))));
			}
		}
		return inStream;
	}
	
	private static class TransformingJarEntry extends JarEntry {
		public TransformingJarEntry(JarEntry je) {
			super(je);
		}
		
		// yeet code signing, because it will be invalid due to transformation
		@Override
		public Certificate[] getCertificates() {
			return null;
		}
		
		@Override
		public CodeSigner[] getCodeSigners() {
			return null;
		}
	}
}
