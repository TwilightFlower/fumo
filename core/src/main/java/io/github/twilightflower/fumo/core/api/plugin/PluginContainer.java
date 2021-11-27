/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
