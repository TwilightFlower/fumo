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
