package io.github.twilightflower.fumo.core.api.data.codec;

import java.util.function.Function;

class FunctionCodec<T, U> implements FumoCodec<T, U> {
	private final Function<? super T, ? extends U> fn;
	private final Class<U> outputType;
	FunctionCodec(Function<? super T, ? extends U> fn, Class<U> ot) {
		this.fn = fn;
		outputType = ot;
	}

	@Override
	public Class<U> getOutputType() {
		return outputType;
	}

	@Override
	public U decode(T data) {
		return fn.apply(data);
	}
}