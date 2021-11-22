package io.github.twilightflower.fumo.core.api;

import java.nio.file.Path;

import io.github.twilightflower.fumo.core.api.mod.ModMetadata;
import io.github.twilightflower.fumo.core.api.plugin.PluginContainer;
import io.github.twilightflower.fumo.core.impl.FumoLoaderImpl;

public interface FumoLoader {
	static FumoLoader getInstance() {
		return FumoLoaderImpl.INSTANCE;
	}
	
	/**
	 * Gets a loaded plugin.
	 * @param pluginId The ID of the plugin to retrieve.
	 * @return Plugin with the provided ID, or null if there is no such plugin loaded.
	 * @throws IllegalStateException if plugins have not yet been loaded.
	 */
	PluginContainer getPlugin(String pluginId) throws IllegalStateException;
	
	/**
	 * Checks if a plugin is loaded.
	 * @param modId The ID of the plugin to check if loaded.
	 * @throws IllegalStateException if plugin have not yet been loaded.
	 */
	default boolean isPluginLoaded(String pluginId) {
		return getPlugin(pluginId) != null;
	}
	
	/**
	 * Gets a loaded mod.
	 * @param modId The ID of the mod to retrieve.
	 * @return Mod with the provided ID, or null if there is no such mod loaded.
	 * @throws IllegalStateException if mods have not yet been loaded.
	 */
	ModMetadata getMod(String modId) throws IllegalStateException;
	
	/**
	 * Checks if a mod is loaded.
	 * @param modId The ID of the mod to check if loaded.
	 * @throws IllegalStateException if mods have not yet been loaded.
	 */
	default boolean isModLoaded(String modId) {
		return getMod(modId) != null;
	}
	
	/**
	 * Gets the main directory to load mods from.
	 */
	Path getModsDir();
	
	/**
	 * Gets the main directory to load configuration files from.
	 */
	Path getConfigDir();
	
	/**
	 * Registers a launch provider.
	 * @param id The ID of the launch provider being registered.
	 * @param provider the launch provider being registered.
	 * @throws IllegalStateException if the target has already been launched.
	 */
	void registerLaunchProvider(FumoIdentifier id, LaunchProvider provider) throws IllegalStateException;
}
