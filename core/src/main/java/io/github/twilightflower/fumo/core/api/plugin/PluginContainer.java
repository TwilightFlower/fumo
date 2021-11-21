package io.github.twilightflower.fumo.core.api.plugin;

import java.nio.file.Path;

import com.github.zafarkhaja.semver.Version;

import io.github.twilightflower.fumo.core.api.data.DataObject;

public interface PluginContainer {
	FumoLoaderPlugin getPlugin();
	String getPluginId();
	String getPluginName();
	Version getVersion();
	DataObject getData();
	Path getRoot();
}
