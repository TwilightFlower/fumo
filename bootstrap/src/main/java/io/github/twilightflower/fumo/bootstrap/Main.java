/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.bootstrap;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.Set;

import io.github.twilightflower.fumo.bootstrap.api.Bootstrapper;

public class Main {

	public static void main(String[] args) throws IOException, InvocationTargetException {
		try(Bootstrapper bootstrap = loadSingleService(Bootstrapper.class)) {
			bootstrap.accept(args);
			Set<URL> loaderPaths = bootstrap.loaderURLs(); 
			Set<Path> targetPaths = bootstrap.targetPaths();
			String[] args2 = bootstrap.args();
			
			double javaVersion = Double.parseDouble(System.getProperty("java.specification.version"));
			ClassLoader jdkLoader = null;
			if(javaVersion > 8) {
				jdkLoader = ClassLoader.getPlatformClassLoader();
			}
			
			ClassLoader loaderLoader = new URLClassLoader(loaderPaths.toArray(new URL[loaderPaths.size()]), jdkLoader);
			try {
				Class<?> fumoMain = loaderLoader.loadClass("io.github.twilightflower.fumo.core.Main");
				Method coreMain = fumoMain.getMethod("bootstrapped", String[].class, Set.class);
				coreMain.invoke(null, args2, targetPaths);
			} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
				throw new RuntimeException("Bootstrap screwed up!", e);
			}
		}
	}
	
	private static <T> T loadSingleService(Class<T> of) {
		ServiceLoader<T> sl = ServiceLoader.load(of);
		int count = 0;
		T serv = null;
		for(T service : sl) {
			serv = service;
			count++;
		}
		
		if(count != 1) {
			throw new RuntimeException(String.format("Error loading service %s: expected exactly one, found %d", of.getName(), count));
		} else {
			return serv;
		}
	}
}
