package io.github.twilightflower.fumo.core.api.plugin;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import io.github.twilightflower.fumo.core.api.FumoLoader;
import io.github.twilightflower.fumo.core.api.transformer.TransformerRegistry;

public interface FumoLoaderPlugin {
	/**
	 * The first method called. Used to pass the loader API interface, which can (and should) be stored.
	 * @param loader The loader interface
	 */
	default void init(FumoLoader loader) {};
	
	/**
	 * Discovers new plugins. Argument maps are unmodifiable. Plugin maps are arrays as version resolution has not yet occurred.
	 * @param modsDir The mods directory.
	 * @param thisRound Plugins discovered since this method was last invoked. Key is plugin ID.
	 * @param all All currently loaded plugins. Key is plugin ID.
	 * @return Paths to new plugin jars. All paths must support toURL for URLClassLoader.
	 */
	default Set<Path> discoverNewPlugins(Path modsDir, Map<String, PluginContainer[]> thisRound, Map<String, PluginContainer[]> all) {
		return Collections.emptySet();
	}
	
	/**
	 * Callback for when plugin loading has finished. Useful for interacting with other plugins.
	 */
	default void pluginsLoaded() {}
	
	/**
	 * Callback for plugins to register transformers. 
	 * @param registry Transformer registry
	 */
	default void registerTransformers(TransformerRegistry registry) {}
}
