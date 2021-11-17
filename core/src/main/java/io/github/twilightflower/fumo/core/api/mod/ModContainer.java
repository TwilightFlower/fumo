package io.github.twilightflower.fumo.core.api.mod;

import java.nio.file.Path;

import com.github.zafarkhaja.semver.Version;

import io.github.twilightflower.fumo.core.api.data.DataObject;

public interface ModContainer {
	String getModId();
	String getModName();
	Version getVersion();
	DataObject getData();
	Path getRoot();
	/**
	 * Should the loader use this -- and only this -- instance, out of all mods with the same mod ID?
	 * This should only be true for mods explicitly installed by the user, as multiple mods with the same mod ID
	 * and this method returning true will cause a mod resolution error.
	 */
	default boolean forceThisInstance() {
		return false;
	}
}
