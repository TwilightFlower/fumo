package io.github.twilightflower.fumo.core.api.data.codec;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.twilightflower.fumo.core.api.data.DataBoolean;
import io.github.twilightflower.fumo.core.api.data.DataInt;
import io.github.twilightflower.fumo.core.api.data.DataNumeric;
import io.github.twilightflower.fumo.core.api.data.DataString;
import io.github.twilightflower.fumo.core.api.util.Pair;
import io.github.twilightflower.fumo.core.impl.util.Util;

public interface FumoCodec<T, U> {
	FumoCodec<DataInt, Integer> INT = function(DataInt::getIntValue, Integer.class);
	FumoCodec<DataNumeric, Double> DOUBLE = function(DataNumeric::getValue, Double.class);
	FumoCodec<DataString, String> STRING = function(DataString::getValue, String.class);
	FumoCodec<DataBoolean, Boolean> BOOLEAN = function(DataBoolean::getValue, Boolean.class);
	
	static <T, U> FumoCodec<T, U> function(Function<? super T, ? extends U> fn, Class<U> outputType) {
		return new FunctionCodec<>(fn, outputType);
	}
	
	@SuppressWarnings("unchecked")
	static <T, U> FumoCodec<T, U> constant(U constant) {
		return function(t -> constant, (Class<U>) constant.getClass());
	}
	
	@SuppressWarnings("unchecked")
	static <T, U> FumoCodec<T, U> coerce(FumoCodec<? super T, ? extends U> from) {
		return (FumoCodec<T, U>) from;
	}
	
	static <T> FumoCodec<T, T> identity(Class<T> type) {
		return function(t -> t, type);
	}
	
	static <T, U> FumoCodec<T, U> defaultVal(FumoCodec<? super T, U> codec, Supplier<U> def) {
		return function(t -> t != null ? codec.decode(t) : def.get(), codec.getOutputType());
	}
	
	static <T, U> FumoCodec<T, U> propogateNull(FumoCodec<? super T, U> codec) {
		return function(t -> t != null ? codec.decode(t) : null, codec.getOutputType());
	}
	
	static <T, U, V> FumoCodec<T, V> compose(FumoCodec<? super T, U> first, FumoCodec<? super U, V> second) {
		return function(t -> second.decode(first.decode(t)), second.getOutputType());
	}
	
	@SuppressWarnings("unchecked")
	static <T, U, X, Y> FumoCodec<Map.Entry<T, U>, Map.Entry<X, Y>> pair(FumoCodec<? super T, ? extends X> leftCodec, FumoCodec<? super U, ? extends Y> rightCodec) {
		return function(t -> Pair.of(leftCodec.decode(t.getKey()), rightCodec.decode(t.getValue())), (Class<Map.Entry<X, Y>>) (Class<?>) Map.Entry.class);
	}
	
	@SuppressWarnings("unchecked")
	static <T, C extends T, U> FumoCodec<T, U> cast(Class<C> castTo, FumoCodec<? super C, U> then) {
		return function(t -> {
			if(!castTo.isAssignableFrom(t.getClass())) {
				throw new ClassCastException(String.format("Value type mismatch: expected %s, found %s", castTo.getName(), t.getClass().getName()));
			}
			return then.decode((C) t);
		}, then.getOutputType());
	}
	
	static <K, V, U> FumoCodec<Map<K, V>, Collection<U>> iterateMap(FumoCodec<Map.Entry<K, V>, U> entryCodec) {
		return iterateMap(entryCodec, ArrayList::new);
	}
	
	@SuppressWarnings("unchecked")
	static <K, V, U, C extends Collection<U>> FumoCodec<Map<K, V>, C> iterateMap(FumoCodec<Map.Entry<K, V>, U> entryCodec, Supplier<C> collectionSupplier) {
		return compose(function(m -> m.entrySet(), (Class<Set<Map.Entry<K, V>>>) (Class<?>) Set.class), iterate(entryCodec, collectionSupplier));
	}
	
	static <K, V, T> FumoCodec<Map<K, V>, Map<K, T>> onValues(FumoCodec<? super V, ? extends T> valueCodec) {
		return onValues(valueCodec, HashMap::new);
	}
	
	@SuppressWarnings("unchecked")
	static <K, V, T, M extends Map<K, T>> FumoCodec<Map<K, V>, M> onValues(FumoCodec<? super V, ? extends T> valueCodec, Supplier<M> mapSupplier) {
		return function((Map<K, V> m) -> {
			M map = mapSupplier.get();
			for(Map.Entry<K, V> entry : m.entrySet()) {
				map.put(entry.getKey(), valueCodec.decode(entry.getValue()));
			}
			return map;
		}, (Class<M>) mapSupplier.get().getClass());
	}
	
	static <T, U> FumoCodec<Iterable<T>, List<U>> iterate(FumoCodec<? super T, ? extends U> elementCodec) {
		return iterate(elementCodec, ArrayList::new);
	}
	
	@SuppressWarnings("unchecked")
	static <T, U, C extends Collection<U>> FumoCodec<Iterable<T>, C> iterate(FumoCodec<? super T, ? extends U> elementCodec, Supplier<C> collectionSupplier) {
		return function(it -> {
			C collection = collectionSupplier.get();
			for(T t : it) {
				collection.add(elementCodec.decode(t));
			}
			return collection;
		}, (Class<C>) collectionSupplier.get().getClass());
	}
	
	@SuppressWarnings("unchecked")
	static <T, U> FumoCodec<T, List<U>> list(FumoCodec<? super T, ? extends U>... codecs) {
		return function(t -> {
			List<U> list = new ArrayList<>();
			for(FumoCodec<? super T, ? extends U> codec : codecs) {
				list.add(codec.decode(t));
			}
			return list;
		}, (Class<List<U>>) (Class<?>) List.class);
	}
	
	@SafeVarargs
	static <T, U> FumoCodec<T, U> multi(MethodHandle mh, Class<U> type, FumoCodec<? super T, ?>... codecs) {
		MethodType mType = mh.type();
		Class<?>[] types = mType.parameterArray();
		if(types.length != codecs.length) {
			throw new RuntimeException(String.format("Provided method has a different number of parameters than codecs provided! (params: %d, codecs: %d", types.length, codecs.length));
		}
		
		for(int i = 0; i < codecs.length; i++) {
			FumoCodec<? super T, ?> codec = codecs[i];
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
	
	static <T, U, K, M extends Map<K, ?>> FumoCodec<M, U> entry(FumoCodec<? super M, K> keyCodec, FumoCodec<? super T, U> entryCodec, Class<T> entryType, boolean require) {
		return function(obj -> {
			K key = keyCodec.decode(obj);
			Object val = obj.get(key);
			if(require) {
				Objects.requireNonNull(val, "Key " + key + " is null or missing");
			}
			if(val == null) {
				return entryCodec.decode(null);
			} else if(!entryType.isAssignableFrom(val.getClass())) {
				throw new RuntimeException(String.format("Entry type mismatch on %s: codec expected %s, found %s", key.toString(), entryType.getName(), val.getClass().getName()));
			} else {
				@SuppressWarnings("unchecked")
				T t = (T) val;
				return entryCodec.decode(t);
			}
		}, entryCodec.getOutputType());
	}
	
	@SuppressWarnings("unchecked")
	static <T, U, K> FumoCodec<T, Map<K, U>> map(FumoCodec<? super T, Pair<K, U>>... entryCodecs) {
		return function(t -> {
			Map<K, U> map = new HashMap<>();
			for(FumoCodec<? super T, Pair<K, U>> codec : entryCodecs) {
				Pair<K, U> val = codec.decode(t);
				map.put(val.left(), val.right());
			}
			return null;
		}, (Class<Map<K, U>>) (Class<?>) Map.class);
	}
	
	Class<U> getOutputType();
	U decode(T data);
}
