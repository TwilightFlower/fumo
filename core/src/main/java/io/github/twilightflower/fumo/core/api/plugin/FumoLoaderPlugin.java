package io.github.twilightflower.fumo.core.api.plugin;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import io.github.twilightflower.fumo.core.api.FumoLoader;
import io.github.twilightflower.fumo.core.api.mod.ModMetadata;
import io.github.twilightflower.fumo.core.api.transformer.TransformerRegistry;

public interface FumoLoaderPlugin {
	/**
	 * The first method called. Used to pass the loader API interface, which can (and should) be stored.
	 * @param loader The loader interface
	 */
	default void init(FumoLoader loader) {}
	
	/**
	 * Discovers new plugins.
	 * @return Plugin containers representing new plugins.
	 */
	default Set<PluginMetadata> discoverPlugins() {
		return Collections.emptySet();
	}
	
	/**
	 * Callback for when plugin loading has finished. Useful for interacting with other plugins.
	 */
	default void pluginsLoaded() {}
	
	/**
	 * 
	 */
	default Set<ModMetadata> discoverMods(Map<String, Set<ModMetadata>> allMods, Set<ModMetadata> newMods) {
		return Collections.emptySet();
	}
	
	default void modsLoaded() {}
	
	/**
	 * Callback for plugins to register transformers. 
	 * @param registry Transformer registry
	 */
	default void registerTransformers(TransformerRegistry registry) {}
	
	/**
	 * Called immediately before launching the target.
	 * @param targetClassLoader The target classloader.
	 */
	default void preLaunch(ClassLoader targetClassLoader) {}
}
