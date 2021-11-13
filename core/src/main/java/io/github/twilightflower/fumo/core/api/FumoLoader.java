package io.github.twilightflower.fumo.core.api;

import io.github.twilightflower.fumo.core.api.plugin.PluginContainer;

public interface FumoLoader {
	/**
	 * @param pluginId The ID of the plugin to retrieve.
	 * @return Plugin with the provided ID, or null if there is no such plugin loaded.
	 * @throws IllegalStateException if plugins have not yet been loaded or this method is called from target-side code.
	 */
	PluginContainer getPlugin(String pluginId) throws IllegalStateException;
	
	/**
	 * Registers a launch provider.
	 * @param id The ID of the launch provider being registered.
	 * @param provider the launch provider being registered.
	 * @throws IllegalStateException if the target has already been launched or this method is called from target-side code.
	 */
	void registerLaunchProvider(Identifier id, LaunchProvider provider) throws IllegalStateException;
}
