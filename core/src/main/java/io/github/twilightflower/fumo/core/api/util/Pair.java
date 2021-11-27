/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.core.api.util;

import java.util.Map;

public class Pair<T, U> implements Map.Entry<T, U> {
	private final T left;
	private final U right;
	private Pair(T left, U right) {
		this.left = left;
		this.right = right;
	}
	
	public static <T, U> Pair<T, U> of(T left, U right) {
		return new Pair<>(left, right);
	}
	
	public T left() {
		return left;
	}
	
	public U right() {
		return right;
	}
	
	@Override
	public T getKey() {
		return left;
	}

	@Override
	public U getValue() {
		return right;
	}

	@Override
	public U setValue(U value) {
		throw new UnsupportedOperationException();
	}
}
