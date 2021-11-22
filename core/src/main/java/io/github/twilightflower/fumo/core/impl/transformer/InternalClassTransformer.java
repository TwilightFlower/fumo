package io.github.twilightflower.fumo.core.impl.transformer;

import java.io.IOException;
import java.io.InputStream;

public interface InternalClassTransformer {
	boolean transforms(String className); // name form: path/to/the/OuterClass$InnerClass
	byte[] transform(String className, InputStream clazz) throws IOException;
}
