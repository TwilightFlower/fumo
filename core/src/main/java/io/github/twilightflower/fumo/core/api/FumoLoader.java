/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.core.api;

import java.nio.file.Path;
import java.util.Collection;
import org.objectweb.asm.tree.ClassNode;

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
	default boolean isPluginLoaded(String pluginId) throws IllegalStateException {
		return getPlugin(pluginId) != null;
	}
	
	/**
	 * Gets all plugins.
	 * @return all loaded plugins
	 * @throws IllegalStateException if plugins have not yet been loaded
	 */
	Collection<PluginContainer> getAllPlugins() throws IllegalStateException;
	
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
	default boolean isModLoaded(String modId) throws IllegalStateException {
		return getMod(modId) != null;
	}
	
	/**
	 * Gets all mods.
	 * @return all loaded mods
	 * @throws IllegalStateException if mods have not yet been loaded
	 */
	Collection<ModMetadata> getAllMods() throws IllegalStateException;
	
	/**
	 * Gets the main directory to load mods from.
	 */
	Path getModsDir();
	
	/**
	 * Gets the main directory to load configuration files from.
	 */
	Path getConfigDir();
	
	/**
	 * Gets the target classloader.
	 * @throws IllegalStateException if target is not yet loaded.
	 */
	ClassLoader getTargetClassloader() throws IllegalStateException;
	
	/**
	 * Gets the classloader plugins are loaded on.
	 * @throws IllegalStateException if plugins are not yet loaded
	 */
	ClassLoader getPluginClassloader() throws IllegalStateException;
	
	/**
	 * Registers a launch provider.
	 * @param id The ID of the launch provider being registered.
	 * @param provider the launch provider being registered.
	 * @throws IllegalStateException if the target has already been launched.
	 */
	void registerLaunchProvider(FumoIdentifier id, LaunchProvider provider) throws IllegalStateException;
	
	boolean transformsClass(String className);
	ClassNode getTransformedClass(String className) throws ClassNotFoundException;
}
