package io.github.twilightflower.fumo.core.api.plugin;

import static io.github.twilightflower.fumo.core.api.data.codec.Codec.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.github.zafarkhaja.semver.Version;

import io.github.twilightflower.fumo.core.api.data.DataObject;
import io.github.twilightflower.fumo.core.api.data.DataString;
import io.github.twilightflower.fumo.core.api.data.codec.Codec;
import io.github.twilightflower.fumo.core.impl.util.Codecs;
import io.github.twilightflower.fumo.core.impl.util.Util;

public final class PluginMetadata {
	public static Codec<DataObject, PluginMetadata> codec(Path root) {
		return multi(
			Util.mhc(PluginMetadata.class), PluginMetadata.class,
			entry(
				constant("id"),
				STRING,
				DataString.class,
				true
			),
			entry(
				constant("name"),
				propogateNull(STRING),
				DataString.class,
				false
			),
			entry(
				constant("plugin-class"),
				STRING,
				DataString.class,
				true
			),
			entry(
				constant("version"),
				compose(STRING, Codecs.VERSION),
				DataString.class,
				true
			),
			identity(DataObject.class),
			constant(root)
		);
	}
	
	private final String id, name, clazz;
	private final Version version;
	private final DataObject data;
	private final Path root;
	private final int hashCode;
	
	public PluginMetadata(String id, String name, String clazz, Version version, DataObject data, Path root) {
		this.id = id;
		this.name = name;
		this.clazz = clazz;
		this.version = version;
		this.data = data;
		this.root = root;
		hashCode = Objects.hash(id, name, clazz, version, root);
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getClazz() {
		return clazz;
	}
	
	public Version getVersion() {
		return version;
	}
	
	public DataObject getData() {
		return data;
	}
	
	public Path getRoot() {
		return root;
	}
	
	public static PluginMetadata parse(Path root) throws IOException {
		try(InputStream inStream = new BufferedInputStream(Files.newInputStream(root.resolve("fumo.plugin.json")))) {
			return parse(root, inStream);
		}
	}
	
	public static PluginMetadata parse(Path root, InputStream json) throws IOException {
		return codec(root).decode(Util.readJson(json));
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof PluginMetadata)) return false;
		PluginMetadata o = (PluginMetadata) other;
		return o.id.equals(id) && o.name.equals(name) && o.clazz.equals(clazz) && o.version.equals(version) & o.root.equals(root);
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
}