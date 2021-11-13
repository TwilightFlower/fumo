package io.github.twilightflower.fumo.core.api.data.codec;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import io.github.twilightflower.fumo.core.api.data.DataEntry;
import io.github.twilightflower.fumo.core.api.data.DataInt;
import io.github.twilightflower.fumo.core.api.data.DataNumeric;
import io.github.twilightflower.fumo.core.api.data.DataObject;
import io.github.twilightflower.fumo.core.api.data.DataString;
import io.github.twilightflower.fumo.core.impl.util.Util;

public interface Codec<T, U> {
	Codec<DataInt, Integer> INT = function(DataInt::getIntValue, Integer.class);
	Codec<DataNumeric, Double> DOUBLE = function(DataNumeric::getValue, Double.class);
	Codec<DataString, String> STRING = function(DataString::getValue, String.class);
	
	static <T, U> Codec<T, U> function(Function<T, U> fn, Class<U> outputType) {
		return new FunctionCodec<>(fn, outputType);
	}
	
	@SuppressWarnings("unchecked") // thank you type erasure -_-, no List<U>.class
	static <T, U> Codec<Iterable<T>, List<U>> iterate(Codec<? super T, ? extends U> elementCodec) {
		return (Codec<Iterable<T>, List<U>>) (Codec<T, ?>) function((Iterable<T> it) -> {
			List<U> list = new ArrayList<>();
			for(T t : it) {
				list.add(elementCodec.decode(t));
			}
			return list;
		}, List.class);
	}
	
	@SuppressWarnings("unchecked")
	static <T, U> Codec<T, List<U>> list(Codec<? super T, ? extends U>... codecs) {
		return (Codec<T, List<U>>) (Codec<T, ?>) function((T t) -> {
			List<U> list = new ArrayList<>();
			for(Codec<? super T, ? extends U> codec : codecs) {
				list.add(codec.decode(t));
			}
			return list;
		}, List.class);
	}
	
	@SafeVarargs
	static <T, U> Codec<T, U> multi(MethodHandle mh, Class<U> type, Codec<? super T, ?>... codecs) {
		MethodType mType = mh.type();
		Class<?>[] types = mType.parameterArray();
		if(types.length != codecs.length) {
			throw new RuntimeException(String.format("Provided method has a different number of parameters than codecs provided! (params: %d, codecs: %d", types.length, codecs.length));
		}
		
		for(int i = 0; i < codecs.length; i++) {
			Codec<? super T, ?> codec = codecs[i];
			if(!types[i].isAssignableFrom(codec.getOutputType())) {
				throw new RuntimeException(String.format("Passed method cannot accept codec's output type at position %d (codec: %s, method: %s)", i, codec.getOutputType().getName(), types[i].getName()));
			}
		}
		
		if(!type.isAssignableFrom(mType.returnType())) {
			throw new RuntimeException(String.format("Passed method does not have a return type matching the codec type. (Codec: %s, method: %s)", type.getName(), mType.returnType().getName()));
		}
		
		// checks have passed
		return function(Util.makeSneakyFunction(t -> {
			Object[] returns = new Object[codecs.length];
			for(int i = 0; i < codecs.length; i++) {
				returns[i] = codecs[i].decode(t);
			}
			@SuppressWarnings("unchecked")
			U ret = (U) mh.invokeWithArguments(returns);
			return ret;
		}), type);
	}
	
	static <T extends DataEntry, U> Codec<DataObject, U> entry(String name, Codec<T, U> entryCodec, Class<T> entryType, boolean require) {
		return function(obj -> {
			DataEntry val = obj.getEntry(name);
			if(require) {
				Objects.requireNonNull(val, "Key " + name + " is null or missing");
			}
			if(val == null) {
				return entryCodec.decode(null);
			} else if(!entryType.isAssignableFrom(val.getClass())) {
				throw new RuntimeException(String.format("Entry type mismatch on %s: codec expected %s, found %s", name, entryType.getName(), val.getClass().getName()));
			} else {
				@SuppressWarnings("unchecked")
				T t = (T) val;
				return entryCodec.decode(t);
			}
		}, entryCodec.getOutputType());
	}
	
	Class<U> getOutputType();
	U decode(T data);
}
