package io.github.twilightflower.fumo.core.api;

import java.lang.reflect.InvocationTargetException;

public interface LaunchProvider {
	void launchTarget(ClassLoader targetLoader, String[] programArgs) throws InvocationTargetException;
}
