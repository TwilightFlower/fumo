/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.core.api.transformer;

import org.objectweb.asm.tree.ClassNode;

public interface ClassTransformer {
	void acceptTransformerContext(TransformerContext context);
	boolean transforms(String className);
	ClassNode transform(String className, ClassNode clazz);
}
