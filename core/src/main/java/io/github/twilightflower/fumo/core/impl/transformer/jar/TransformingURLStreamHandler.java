package io.github.twilightflower.fumo.core.impl.transformer.jar;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.jar.JarFile;

public class TransformingURLStreamHandler extends URLStreamHandler implements URLStreamHandlerFactory {
	private final InternalClassTransformer transformer;
	public TransformingURLStreamHandler(InternalClassTransformer transformer) {
		this.transformer = transformer;
	}
	
	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		return new TransformingJarURLConnection(u);
	}
	
	@Override
	public URLStreamHandler createURLStreamHandler(String protocol) {
		if(!protocol.equals("jar")) {
			throw new IllegalArgumentException("Unexpected (non-jar) protocol " + protocol);
		} else {
			return this;
		}
	}
	
	private class TransformingJarURLConnection extends JarURLConnection {
		private final File loc;
		private JarFile jar;
		private TransformingJarURLConnection(URL url) throws IOException {
			super(url);
			loc = new File(url.getFile());
		}

		@Override
		public JarFile getJarFile() throws IOException {
			return jar;
		}

		@Override
		public void connect() throws IOException {
			if(jar == null) {
				jar = new TransformingJarFile(loc, transformer);
			}
		}
		
	}
}
