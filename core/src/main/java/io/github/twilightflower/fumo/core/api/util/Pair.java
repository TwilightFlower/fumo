package io.github.twilightflower.fumo.core.api.util;

import java.util.Map;

public class Pair<T, U> implements Map.Entry<T, U> {
	private final T left;
	private final U right;
	private Pair(T left, U right) {
		this.left = left;
		this.right = right;
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
