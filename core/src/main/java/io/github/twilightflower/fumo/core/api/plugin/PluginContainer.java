package io.github.twilightflower.fumo.core.api.plugin;

import com.github.zafarkhaja.semver.Version;

public interface PluginContainer {
	FumoLoaderPlugin getPlugin();
	String getPluginId();
	String getPluginName();
	Version getVersion();
}
