package io.github.twilightflower.fumo.core.api;

public interface LaunchProvider {
	boolean isActive(ClassLoader targetLoader, String[] programArgs);
	Class<?> getMainClass(ClassLoader targetLoader);
	String[] affectArgs(ClassLoader targetLoader, String[] programArgs);
}
