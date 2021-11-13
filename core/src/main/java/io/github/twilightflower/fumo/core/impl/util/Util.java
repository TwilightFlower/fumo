package io.github.twilightflower.fumo.core.impl.util;

import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Util {
	public static <T, U> Function<T, U> makeSneakyFunction(ExcFn<T, U, ?> ex) {
		@SuppressWarnings("unchecked")
		ExcFn<T, U, RuntimeException> ex2 = (ExcFn<T, U, RuntimeException>) ex;
		return ex2::apply;
	}
	
	
	private static final Function<URI, URL> URI_URL = makeSneakyFunction(URI::toURL);
	public static URL uriToURL(URI uri) {
		return URI_URL.apply(uri);
	}
	
	public static <T> Enumeration<T> iteratorEnumeration(Iterator<T> iter) {
		return new IteratorEnumeration<>(iter);
	}
	
	public static Map<String, ?> deepJsonToMap(JsonObject object) {
		Map<String, Object> map = new HashMap<>();
		for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
			JsonElement v = entry.getValue();
			String k = entry.getKey();
			if(v.isJsonObject()) {
				map.put(k, deepJsonToMap(v.getAsJsonObject()));
			} else if(v.isJsonArray()) {
				for(JsonElement j : v.getAsJsonArray()) {
					if(j.isJsonObject()) {
						
					}
				}
			}
		}
		return map;
	}
	
	public interface ExcFn<T, U, E extends Throwable> {
		U apply(T t) throws E;
	}
	
	private static class IteratorEnumeration<T> implements Enumeration<T> {
		final Iterator<T> iterator;
		
		IteratorEnumeration(Iterator<T> iter) {
			iterator = iter;
		}

		@Override
		public boolean hasMoreElements() {
			return iterator.hasNext();
		}

		@Override
		public T nextElement() {
			return iterator.next();
		}
	}
}
