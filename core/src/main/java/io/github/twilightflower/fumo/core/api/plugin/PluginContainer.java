package io.github.twilightflower.fumo.core.api.plugin;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.zafarkhaja.semver.Version;

import io.github.twilightflower.fumo.core.api.data.DataObject;
import io.github.twilightflower.fumo.core.impl.util.Codecs;
import io.github.twilightflower.fumo.core.impl.util.Util;

public interface PluginContainer {
	FumoLoaderPlugin getPlugin();
	String getPluginId();
	String getPluginName();
	Version getVersion();
	DataObject getData();
	Path getRoot();
	
	/*static PluginContainer loadPlugin(Path rootFrom) {
		Path pluginJson = rootFrom.resolve("fumo.plugin.json");
		try(InputStream in = new BufferedInputStream(Files.newInputStream(pluginJson))) {
			DataObject pluginData = Util.readJson(in);
			PluginMetadata pluginMetadata = Codecs.PLUGIN_METADATA.decode(pluginData);
			pluginMetadata.loc = rootFrom;
			return pluginMetadata.l
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error loading plugin from %s", pluginJson.toString()), e);
		}
	}*/
}
