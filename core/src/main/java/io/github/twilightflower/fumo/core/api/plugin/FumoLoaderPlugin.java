package io.github.twilightflower.fumo.core.api.plugin;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import io.github.twilightflower.fumo.core.api.FumoLoader;
import io.github.twilightflower.fumo.core.api.transformer.TransformerRegistry;

public interface FumoLoaderPlugin {
	void acceptLoader(FumoLoader loader);
	
	/**
	 * Discovers new plugins. Argument maps are unmodifiable.
	 * @param modsDir The mods directory.
	 * @param thisRound Plugins discovered since this method was last invoked.
	 * @param all All currently loaded plugins.
	 * @return Paths to new plugin jars. All paths must support toURL for URLClassLoader.
	 */
	Set<Path> discoverNewPlugins(Path modsDir, Map<String, PluginContainer> thisRound, Map<String, PluginContainer> all);
	
	
	
	void registerTransformers(TransformerRegistry registry);
}
