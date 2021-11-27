package io.github.twilightflower.fumo.core.api.transformer;

import org.objectweb.asm.tree.ClassNode;

public interface TransformerContext {
	/**
	 * Gets the given class's node in the state it will be passed to this transformer.
	 * @param className Name of the class to get
	 * @return the class
	 * @throws ClassNotFoundException if there is no such class on the transformation path
	 */
	ClassNode getOtherClass(String className) throws ClassNotFoundException;
	
	ClassNode getUntransformedClass(String className) throws ClassNotFoundException;
	
	void computeMaxs(String className);
	void computeFrames(String className);
}
