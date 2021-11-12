package io.github.twilightflower.fumo.core.api.transformer;

import java.util.Collections;
import java.util.Set;

import io.github.twilightflower.fumo.core.api.Identifier;

public interface TransformerRegistry {
	default void registerTransformer(Identifier id, ClassTransformer transformer) {
		registerTransformer(id, transformer, Collections.emptySet());
	}
	default void registerTransformer(Identifier id, ClassTransformer transformer, Set<Identifier> runBefore) {
		registerTransformer(id, transformer, runBefore, Collections.emptySet());
	}
	void registerTransformer(Identifier id, ClassTransformer transformer, Set<Identifier> runBefore, Set<Identifier> runAfter);
}
