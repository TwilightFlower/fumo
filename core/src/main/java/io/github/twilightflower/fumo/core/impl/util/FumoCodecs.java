/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.core.impl.util;

import static io.github.twilightflower.fumo.core.api.data.codec.FumoCodec.*;

import java.util.Map;

import com.github.zafarkhaja.semver.Version;
import com.github.zafarkhaja.semver.expr.Expression;

import io.github.twilightflower.fumo.core.api.data.DataObject;
import io.github.twilightflower.fumo.core.api.data.DataString;
import io.github.twilightflower.fumo.core.api.data.codec.FumoCodec;

public class FumoCodecs {
	public static final FumoCodec<String, Version> VERSION = function(Version::valueOf, Version.class);
	public static final FumoCodec<String, Expression> VERSION_EXPR = function(Util::parseVersionExpression, Expression.class);
	public static final FumoCodec<DataObject, Map<String, String>> STRING_MAP = coerce(onValues(cast(DataString.class, STRING)));
}
