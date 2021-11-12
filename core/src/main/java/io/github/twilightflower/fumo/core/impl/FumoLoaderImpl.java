package io.github.twilightflower.fumo.core.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FumoLoaderImpl {
	private final Path modsDir;
	
	public FumoLoaderImpl() {
		modsDir = Paths.get("mods");
		
	}
}
