package io.github.twilightflower.fumo.core.impl.plugin;

import com.github.zafarkhaja.semver.Version;

import io.github.twilightflower.fumo.core.api.plugin.FumoLoaderPlugin;
import io.github.twilightflower.fumo.core.api.plugin.PluginContainer;

public class PluginContainerImpl implements PluginContainer {
	private final String id, name;
	private final FumoLoaderPlugin plugin;
	private final Version version;
	
	public PluginContainerImpl(String id, String name, Version version, FumoLoaderPlugin plugin) {
		this.id = id;
		this.name = name;
		this.plugin = plugin;
		this.version = version;
	}

	@Override
	public FumoLoaderPlugin getPlugin() {
		return plugin;
	}

	@Override
	public String getPluginId() {
		return id;
	}

	@Override
	public String getPluginName() {
		return name;
	}
	
	@Override
	public Version getVersion() {
		return version;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof PluginContainer)) return false;
		PluginContainer o = (PluginContainer) other;
		return o.getPluginId().equals(id) && o.getVersion().equals(version);
	}
}
