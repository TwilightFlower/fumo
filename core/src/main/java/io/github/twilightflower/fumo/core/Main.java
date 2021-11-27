/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.core;

import java.nio.file.Path;
import java.util.Set;

import io.github.twilightflower.fumo.core.impl.FumoLoaderImpl;

public class Main {
	public static void bootstrapped(String[] programArgs, Set<Path> targetClasspath) {
		FumoLoaderImpl.bootstrapped(programArgs, targetClasspath);
	}
}
