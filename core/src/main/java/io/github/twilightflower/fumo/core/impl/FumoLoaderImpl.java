package io.github.twilightflower.fumo.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import io.github.twilightflower.fumo.core.api.mod.ModMetadata;
import io.github.twilightflower.fumo.core.api.plugin.FumoLoaderPlugin;
import io.github.twilightflower.fumo.core.api.plugin.PluginContainer;
import io.github.twilightflower.fumo.core.api.plugin.PluginMetadata;
import io.github.twilightflower.fumo.core.impl.plugin.PluginContainerImpl;
import io.github.twilightflower.fumo.core.impl.transformer.FumoTransformerImpl;
import io.github.twilightflower.fumo.core.impl.transformer.TransformerGraphBuilder;
import io.github.twilightflower.fumo.core.impl.transformer.jar.InternalClassTransformer;
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
	private final Map<String, ModMetadata> mods = new HashMap<>();
	private final Map<Identifier, LaunchProvider> launchProviders = new HashMap<>();
	private InternalClassTransformer classTransformer;
	
	public static void bootstrapped(String[] args, Set<Path> targetPaths) {
		FumoLoaderImpl loader = new FumoLoaderImpl();
		loader.loadPlugins();
		loader.loadMods();
		loader.registerTransformers();
		loader.launch(args, targetPaths);
	}
	
	public FumoLoaderImpl() {
		modsDir = Paths.get("mods");
		configDir = Paths.get("config");
	}
	
	private void loadClasspathPlugins() {
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
		state.ensureIsBefore(LoaderState.LOADING_PLUGINS);
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
	
	public void loadMods() {
		state.ensureIsBefore(LoaderState.LOADING_MODS);
		state = LoaderState.LOADING_MODS;
		
		Map<String, Set<ModMetadata>> modCandidates = new HashMap<>();
		Set<ModMetadata> lastMods = new HashSet<>();
		do {
			Set<ModMetadata> newMods = new HashSet<>();
			Map<String, Set<ModMetadata>> lastCandidates = new HashMap<>();
			lastCandidates.putAll(modCandidates);
			lastCandidates = Collections.unmodifiableMap(lastCandidates);
			for(PluginContainer pluginContainer : plugins.values()) {
				FumoLoaderPlugin plugin = pluginContainer.getPlugin();
				for(ModMetadata mod : plugin.discoverMods(lastCandidates, lastMods)) {
					modCandidates.computeIfAbsent(mod.getId(), k -> new HashSet<>()).add(mod);
					newMods.add(mod);
				}
			}
			lastMods = Collections.unmodifiableSet(newMods);
		} while(!lastMods.isEmpty());
		
		for(Set<ModMetadata> candidates : modCandidates.values()) {
			ModMetadata mod = pickModCandidate(candidates);
			mods.put(mod.getId(), mod);
		}
		
		state = LoaderState.LOADED_MODS;
		
		for(PluginContainer pluginContainer : plugins.values()) {
			pluginContainer.getPlugin().modsLoaded();
		}
	}
	
	public void registerTransformers() {
		state.ensureIsBefore(LoaderState.REGISTERING_TRANSFORMERS);
		state = LoaderState.REGISTERING_TRANSFORMERS;
		
		TransformerGraphBuilder transformerBuilder = new TransformerGraphBuilder();
		
		for(PluginContainer pluginContainer : plugins.values()) {
			pluginContainer.getPlugin().registerTransformers(transformerBuilder);
		}
		
		classTransformer = new FumoTransformerImpl(transformerBuilder.resolve());
	}
	
	public void launch(String[] programArgs, Set<Path> targetPaths) {
		state.ensureIsBefore(LoaderState.PRELAUNCH);
		state = LoaderState.PRELAUNCH;
		
		Set<Path> targetRoots = new HashSet<>();
		targetRoots.addAll(targetPaths);
		
		for(ModMetadata mod : mods.values()) {
			targetRoots.add(mod.getRoot());
		}
		
		TransformingNioClassLoader targetLoader = new TransformingNioClassLoader(targetRoots, null, classTransformer);
		
		for(PluginContainer pluginContainer : plugins.values()) {
			pluginContainer.getPlugin().preLaunch(targetLoader);
		}
		
		LaunchProvider launchProvider = chooseLaunchProvider(targetLoader, programArgs);
		
		Class<?> mainClass = launchProvider.getMainClass(targetLoader);
		try {
			Method main = mainClass.getMethod("main", String.class);
			main.invoke(null, (Object) programArgs);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException e) {
			throw new RuntimeException("Exception launching target", e);
		} catch(InvocationTargetException e) {
			throw new RuntimeException(e.getCause());
		}
	}
	
	private LaunchProvider chooseLaunchProvider(ClassLoader target, String[] programArgs) {
		if(System.getProperties().containsKey("fumo.launch.provider")) {
			Identifier sysProperty = Identifier.fromString(System.getProperty("fumo.launch.provider"));
			LaunchProvider lp = launchProviders.get(sysProperty);
			if(lp == null) {
				throw new RuntimeException(String.format("Launch provider %s explicitly used via system property, but not installed!", sysProperty.toString()));
			}
			if(!lp.isActive(target, programArgs) && !System.getProperty("fumo.launch.provider.force", "false").equals("true")) {
				throw new RuntimeException(String.format("Launch provider %s explicitly used via system property, but not active!", sysProperty.toString()));
			}
			return lp;
		}
		
		for(LaunchProvider lp : launchProviders.values()) {
			if(lp.isActive(target, programArgs)) {
				return lp;
			}
		}
		throw new RuntimeException("Could not find an active launch provider!");
	}
	
	private ModMetadata pickModCandidate(Set<ModMetadata> candidates) {
		ModMetadata mod = null;
		boolean forced = false;
		for(ModMetadata candidate : candidates) {
			if(candidate.isForced()) {
				if(forced) {
					throw new RuntimeException("Mod resolution error: multiple user-installed mods with ID " + candidate.getId());
				}
				
				mod = candidate;
				forced = true;
			} else if(mod != null) {
				if(mod.getVersion().compareTo(candidate.getVersion()) < 0) {
					mod = candidate;
				}
			} else {
				mod = candidate;
			}
		}
		return mod;  
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
			plugin.init(this);
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
		state.ensureIsBefore(LoaderState.PRELAUNCH);
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
