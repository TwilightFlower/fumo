package io.github.twilightflower.fumo.core.impl.plugin;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.jimfs.Jimfs;

import io.github.twilightflower.fumo.core.api.FumoLoader;
import io.github.twilightflower.fumo.core.api.data.DataList;
import io.github.twilightflower.fumo.core.api.data.DataObject;
import io.github.twilightflower.fumo.core.api.data.DataString;
import io.github.twilightflower.fumo.core.api.data.codec.FumoCodec;
import io.github.twilightflower.fumo.core.api.mod.ModMetadata;
import io.github.twilightflower.fumo.core.api.plugin.FumoLoaderPlugin;
import io.github.twilightflower.fumo.core.api.plugin.PluginMetadata;
import io.github.twilightflower.fumo.core.impl.util.Util;

import static io.github.twilightflower.fumo.core.api.data.codec.FumoCodec.*;

/**
 * Handles loading other plugins and mods from the mods folder.
 */
public class BasePlugin implements FumoLoaderPlugin {
	private static final FumoCodec<DataObject, Set<String>> JIJ_CODEC = 
		entry(
			constant("fumo"),
			entry(
				constant("jij"),
				iterate(
					cast(DataString.class, STRING),
					HashSet::new
				),
				DataList.class, true
			),
			DataObject.class, true
		);
	
	private FumoLoader loader;
	private boolean loadedMods = false;
	private final FileSystem jijExtractFs = Jimfs.newFileSystem();
	
	@Override
	public void init(FumoLoader loader) {
		this.loader = loader;
		try {
			Files.createDirectory(jijExtractFs.getPath("jij_extract"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Set<PluginMetadata> discoverPlugins() {
		Path modsFolder = loader.getModsDir();
		try {
			Set<PluginMetadata> plugins = new HashSet<>();
			for(Path p : Util.getDirectoryEntries(modsFolder)) {
				if(Files.isRegularFile(p) && p.toString().endsWith(".jar")) {
					// info: cannot use Path-only version of newFileSystem, that was added in java 13
					FileSystem zipFs = FileSystems.newFileSystem(p, getClass().getClassLoader());
					Path fpj = zipFs.getPath("fumo.plugin.json");
					if(Files.isRegularFile(fpj)) {
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
	
	@Override
	public Set<ModMetadata> discoverMods(Map<String, Set<ModMetadata>> allMods, Set<ModMetadata> newMods) {
		Set<ModMetadata> discovered = new HashSet<>();
		if(!loadedMods) {
			loadedMods = true;
			try {
				for(Path p : Util.getDirectoryEntries(loader.getModsDir())) {
					ModMetadata loaded = tryLoadModFromJar(p, true);
					if(loaded != null) {
						discovered.add(loaded);
					}
				}
			} catch(IOException e) {
				throw new RuntimeException("Exception loading mods", e);
			}
		}
		
		// jij stuff
		for(ModMetadata mod : newMods) {
			Set<String> jijNames = JIJ_CODEC.decode(mod.getData());
			Path extractTo = jijExtractFs.getPath(mod.getId());
			Path jijFolder = mod.getRoot().resolve("META-INF").resolve("fumo").resolve("jij");
			try {
				Files.createDirectories(extractTo);
				for(String jij : jijNames) {
					// zipfs cannot be nested, for some reason.
					Path jarNested = jijFolder.resolve(jij);
					Path jarInMemory = extractTo.resolve(jij);
					Files.copy(jarNested, jarInMemory);
					ModMetadata loaded = tryLoadModFromJar(jarInMemory, false);
					if(loaded != null) {
						discovered.add(loaded);
					}
				}
			} catch(IOException e) {
				throw new RuntimeException("Exception loading jar-in-jar mods for mod " + mod.getId(), e);
			}
		}
		
		return discovered;
	}
	
	private ModMetadata tryLoadModFromJar(Path p, boolean forced) throws IOException {
		if(Files.isRegularFile(p) && p.toString().endsWith(".jar")) {
			FileSystem zipFs = FileSystems.newFileSystem(p, getClass().getClassLoader());
			Path fmj = zipFs.getPath("fumo.mod.json");
			if(Files.isRegularFile(fmj)) {
				return ModMetadata.parse(p, forced);
			} else {
				zipFs.close();
			}
		}
		return null;
	}
}
