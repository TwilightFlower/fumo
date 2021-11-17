package io.github.twilightflower.fumo.core.impl.plugin;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import io.github.twilightflower.fumo.core.api.FumoLoader;
import io.github.twilightflower.fumo.core.api.plugin.FumoLoaderPlugin;
import io.github.twilightflower.fumo.core.api.plugin.PluginMetadata;

/**
 * Handles loading other plugins.
 */
public class BasePlugin implements FumoLoaderPlugin {
	private FumoLoader loader;
	
	@Override
	public void init(FumoLoader loader) {
		this.loader = loader;
	}
	
	@Override
	public Set<PluginMetadata> discoverPlugins() {
		Path modsFolder = loader.getModsDir();
		try {
			Set<PluginMetadata> plugins = new HashSet<>();
			for(Path p : Files.newDirectoryStream(modsFolder)) {
				if(Files.isRegularFile(p) && p.toString().endsWith(".jar")) {
					// info: cannot use Path-only version of newFileSystem, that was added in java 13
					FileSystem zipFs = FileSystems.newFileSystem(p, getClass().getClassLoader());
					Path fpj = zipFs.getPath("fumo.plugin.json");
					if(Files.exists(fpj)) {
						plugins.add(PluginMetadata.parse(p));
					} else {
						zipFs.close();
					}
				}
			}
			return plugins;
		} catch (IOException e) {
			throw new RuntimeException("Exception loading plugins", e);
		}
	}
}
