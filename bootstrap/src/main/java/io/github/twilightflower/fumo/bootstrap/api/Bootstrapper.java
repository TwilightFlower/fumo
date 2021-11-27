/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.bootstrap.api;

import java.io.Closeable;
import java.net.URL;
import java.nio.file.Path;
import java.util.Set;

/**
 * Service-provider interface, which defines splitting the classpath between loader classpath and target classpath.
 */
public interface Bootstrapper extends Closeable, AutoCloseable {
	/**
	 * Accepts the program arguments and uses them, in addition to the current (bootstrap) classpath, to compute the loader and target classpaths.
	 * @param args program arguments
	 */
	void accept(String[] args);
	
	/**
	 * 
	 * @return URLs representing classpath entries for the loader side.
	 */
	Set<URL> loaderURLs();
	
	/**
	 * 
	 * @return Paths representing classpath entries for the target side. (Represents the roots of filesystems to load.)
	 */
	Set<Path> targetPaths();
	
	/**
	 * 
	 * @return args to pass the program, with arguments for the bootstrapper removed
	 */
	String[] args();
}
