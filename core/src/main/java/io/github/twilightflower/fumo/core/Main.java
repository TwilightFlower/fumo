package io.github.twilightflower.fumo.core;

import java.nio.file.Path;
import java.util.Set;

import io.github.twilightflower.fumo.core.impl.FumoLoaderImpl;

public class Main {
	public static void bootstrapped(String[] programArgs, Set<Path> targetClasspath) {
		FumoLoaderImpl.bootstrapped(programArgs, targetClasspath);
	}
}
