package io.github.twilightflower.fumo.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.twilightflower.fumo.core.api.FumoLoader;
import io.github.twilightflower.fumo.core.api.Identifier;
import io.github.twilightflower.fumo.core.api.LaunchProvider;
import io.github.twilightflower.fumo.core.api.LoaderState;
import io.github.twilightflower.fumo.core.api.plugin.FumoLoaderPlugin;
import io.github.twilightflower.fumo.core.api.plugin.PluginContainer;
import io.github.twilightflower.fumo.core.api.plugin.PluginMetadata;
import io.github.twilightflower.fumo.core.impl.plugin.PluginContainerImpl;
import io.github.twilightflower.fumo.core.impl.transformer.jar.TransformingNioClassLoader;
import io.github.twilightflower.fumo.core.impl.util.KOTHMap;

/**
 * The loader-side implementation of FumoLoader.
 */
public class FumoLoaderImpl implements FumoLoader {
	private LoaderState state = LoaderState.INIT;
	private final Path modsDir;
	private final Path configDir;
	private final Map<String, PluginContainer> plugins = new HashMap<>();
	private final Map<Identifier, LaunchProvider> launchProviders = new HashMap<>();
	
	public static void main(String[] args) {
		FumoLoaderImpl loader = new FumoLoaderImpl();
		loader.loadPlugins();
	}
	
	public FumoLoaderImpl() {
		modsDir = Paths.get("mods");
		configDir = Paths.get("config");
	}
	
	public void loadClasspathPlugins() {
		try {
			Enumeration<URL> urls = getClass().getClassLoader().getResources("fumo.plugin.json");
			while(urls.hasMoreElements()) {
				URL url = urls.nextElement();
				try(InputStream in = url.openStream()) {
					Path pluginRoot = Paths.get(url.toURI()).getParent();
					PluginMetadata plugin = PluginMetadata.parse(pluginRoot, in);
					plugins.put(plugin.getId(), loadPluginFromMeta(plugin, getClass().getClassLoader()));
				}
			}
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void loadPlugins() {
		state = LoaderState.LOADING_PLUGINS;
		loadClasspathPlugins();
		
		TransformingNioClassLoader pluginClassLoader = new TransformingNioClassLoader(Collections.emptyList(), getClass().getClassLoader());
		
		Set<PluginContainer> askForPlugins = new HashSet<>();
		askForPlugins.addAll(plugins.values());
		while(!askForPlugins.isEmpty()) {
			KOTHMap<String, PluginMetadata> currentlyLoading = new KOTHMap<>((p1, p2) -> p1.getVersion().compareTo(p2.getVersion()));
			
			for(PluginContainer p : askForPlugins) {
				for(PluginMetadata meta : p.getPlugin().discoverPlugins()) {
					currentlyLoading.put(meta.getId(), meta);
				}
			}
			askForPlugins.clear();
			
			for(Map.Entry<String, PluginMetadata> entry : currentlyLoading.entrySet()) {
				if(!plugins.containsKey(entry.getKey())) {
					pluginClassLoader.addRoot(entry.getValue().getRoot());
					PluginContainer loaded = loadPluginFromMeta(entry.getValue(), pluginClassLoader);
					plugins.put(entry.getKey(), loaded);
					askForPlugins.add(loaded);
				} else {
					System.out.println(String.format("Skipping plugin with id %s as a version of it is already loaded", entry.getValue().getId()));
				}
			}
		}
		state = LoaderState.LOADED_PLUGINS;
		
		for(PluginContainer plugin : plugins.values()) {
			plugin.getPlugin().pluginsLoaded();
		}
	}
	
	private PluginContainer loadPluginFromMeta(PluginMetadata metadata, ClassLoader loadOn) {
		try {
			Class<?> pluginClass = loadOn.loadClass(metadata.getClazz());
			if(!FumoLoaderPlugin.class.isAssignableFrom(pluginClass)) {
				throw new ClassCastException(String.format("Plugin class %s (from plugin %s, id %s) does not extend FumoLoaderPlugin", pluginClass.getName(), metadata.getName(), metadata.getId()));
			}
			
			Constructor<?> ctor = pluginClass.getDeclaredConstructor();
			ctor.setAccessible(true);
			FumoLoaderPlugin plugin = (FumoLoaderPlugin) ctor.newInstance();
			return new PluginContainerImpl(metadata.getId(), metadata.getName(), metadata.getVersion(), plugin, metadata.getData(), metadata.getRoot());
		} catch (Throwable e) {
			throw new RuntimeException(String.format("Error initializing plugin %s", metadata.getId()), e);
		}
	}

	@Override
	public PluginContainer getPlugin(String pluginId) throws IllegalStateException {
		state.ensureAccessPlugins();
		return plugins.get(pluginId);
	}

	@Override
	public void registerLaunchProvider(Identifier id, LaunchProvider provider) throws IllegalStateException {
		if(state == LoaderState.LAUNCHED) {
			throw new IllegalStateException("cannot add launch providers after game is launched");
		}
		launchProviders.put(id, provider);
	}

	@Override
	public Path getModsDir() {
		return modsDir;
	}

	@Override
	public Path getConfigDir() {
		return configDir;
	}
}
