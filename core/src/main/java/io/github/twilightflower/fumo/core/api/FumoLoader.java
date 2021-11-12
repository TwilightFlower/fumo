package io.github.twilightflower.fumo.core.api;

import java.util.Map;

import io.github.twilightflower.fumo.core.api.plugin.PluginContainer;

public interface FumoLoader {
	/**
	 * 
	 * @return Unmodifiable map of the loaded plugins.
	 * @throws IllegalStateException if plugins have not yet been loaded or this method is called from target-side code.
	 */
	Map<String, PluginContainer> getPlugins() throws IllegalStateException;
}
