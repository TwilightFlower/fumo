package io.github.twilightflower.fumo.core.impl.transformer;

import java.util.function.Function;

public interface InternalClassTransformer {
	void acceptClassGetter(Function<String, byte[]> resGetter);
	boolean transforms(String className);
	byte[] getTransformed(String className) throws ClassNotFoundException;
}
