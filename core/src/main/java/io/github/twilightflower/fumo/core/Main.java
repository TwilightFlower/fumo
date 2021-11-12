package io.github.twilightflower.fumo.core;

import java.net.URL;
import java.util.Set;

import io.github.twilightflower.fumo.core.impl.FumoLoaderImpl;

public class Main {
	public static void bootstrapped(String[] programArgs, Set<URL> targetClasspath) {
		FumoLoaderImpl loader = new FumoLoaderImpl();
	}
}
