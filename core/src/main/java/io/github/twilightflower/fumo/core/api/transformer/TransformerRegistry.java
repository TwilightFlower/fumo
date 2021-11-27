/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.core.api.transformer;

import java.util.Collections;
import java.util.Set;

import io.github.twilightflower.fumo.core.api.FumoIdentifier;

public interface TransformerRegistry {
	default void registerTransformer(FumoIdentifier id, ClassTransformer transformer) {
		registerTransformer(id, transformer, Collections.emptySet());
	}
	default void registerTransformer(FumoIdentifier id, ClassTransformer transformer, Set<FumoIdentifier> runBefore) {
		registerTransformer(id, transformer, runBefore, Collections.emptySet());
	}
	void registerTransformer(FumoIdentifier id, ClassTransformer transformer, Set<FumoIdentifier> runBefore, Set<FumoIdentifier> runAfter);
}
