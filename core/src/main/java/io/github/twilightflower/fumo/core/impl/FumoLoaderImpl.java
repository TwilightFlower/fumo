package io.github.twilightflower.fumo.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

public class FumoLoaderImpl {
	private final Path modsDir;
	
	public static void main(String[] args) {
		new FumoLoaderImpl();
	}
	
	public FumoLoaderImpl() {
		modsDir = Paths.get("mods");
		Collection<Path> plugins = new ArrayList<>();
		
		try {
			Enumeration<URL> pluginjsons = getClass().getClassLoader().getResources("fumo.plugin.json");
			while(pluginjsons.hasMoreElements()) {
				URL jsonUrl = pluginjsons.nextElement();
				try(InputStream inStream = jsonUrl.openStream()) {
					
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
