/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.core.impl.transformer;

import java.util.function.Function;

public interface InternalClassTransformer {
	void acceptClassGetter(Function<String, byte[]> resGetter);
	boolean transforms(String className);
	byte[] getTransformed(String className) throws ClassNotFoundException;
}
