package io.github.twilightflower.fumo.core.impl.util;

import static io.github.twilightflower.fumo.core.api.data.codec.Codec.*;

import com.github.zafarkhaja.semver.Version;

import io.github.twilightflower.fumo.core.api.data.DataObject;
import io.github.twilightflower.fumo.core.api.data.DataString;
import io.github.twilightflower.fumo.core.api.data.codec.Codec;
import io.github.twilightflower.fumo.core.api.plugin.PluginMetadata;

public class Codecs {
	public static final Codec<String, Version> VERSION = function(Version::valueOf, Version.class);
}
