package io.github.twilightflower.fumo.core.api.transformer;

import org.objectweb.asm.tree.ClassNode;

public interface ClassTransformer {
	void acceptTransformerContext(TransformerContext context);
	boolean transforms(String className);
	ClassNode transform(String className, ClassNode clazz);
}
