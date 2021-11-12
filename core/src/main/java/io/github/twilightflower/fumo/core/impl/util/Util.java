package io.github.twilightflower.fumo.core.impl.util;

import java.util.function.Function;

public class Util {
	public static <T, U> Function<T, U> makeSneakyFunction(ExcFn<T, U, ?> ex) {
		@SuppressWarnings("unchecked")
		ExcFn<T, U, RuntimeException> ex2 = (ExcFn<T, U, RuntimeException>) ex;
		return ex2::apply;
	}
	
	public interface ExcFn<T, U, E extends Throwable> {
		U apply(T t) throws E;
	}
}
