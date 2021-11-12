package io.github.twilightflower.fumo.bootstrap.api;

import java.io.Closeable;
import java.net.URL;
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
	 * @return URLs representing classpath entries for the target side.
	 */
	Set<URL> targetURLs();
	
	/**
	 * 
	 * @return args to pass the program, with arguments for the bootstrapper removed
	 */
	String[] args();
}
