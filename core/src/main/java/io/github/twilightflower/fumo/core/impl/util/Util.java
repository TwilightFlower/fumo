package io.github.twilightflower.fumo.core.impl.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.github.zafarkhaja.semver.Parser;
import com.github.zafarkhaja.semver.expr.Expression;
import com.github.zafarkhaja.semver.expr.ExpressionParser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import io.github.twilightflower.fumo.core.api.data.DataBoolean;
import io.github.twilightflower.fumo.core.api.data.DataEntry;
import io.github.twilightflower.fumo.core.api.data.DataInt;
import io.github.twilightflower.fumo.core.api.data.DataList;
import io.github.twilightflower.fumo.core.api.data.DataNumeric;
import io.github.twilightflower.fumo.core.api.data.DataObject;
import io.github.twilightflower.fumo.core.api.data.DataString;

public class Util {
	private static final Gson GSON = new Gson();
	private static final Parser<Expression> VERSION_EXPR_PARSER = ExpressionParser.newInstance();
	
	public static <T, U> Function<T, U> makeSneakyFunction(ExcFn<T, U, ?> ex) {
		@SuppressWarnings("unchecked")
		ExcFn<T, U, RuntimeException> ex2 = (ExcFn<T, U, RuntimeException>) ex;
		return ex2::apply;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T sneakily(ExcSupplier<T, ?> sup) {
		return ((ExcSupplier<T, ? extends RuntimeException>) sup).get();
	}
	
	public static Expression parseVersionExpression(String expr) {
		return VERSION_EXPR_PARSER.parse(expr);
	}
	
	public static MethodHandle mh(Class<?> on, String name) {
		for(Method m : on.getDeclaredMethods()) {
			if(m.getName().equals(name)) {
				m.setAccessible(true);
				return sneakily(() -> MethodHandles.lookup().unreflect(m));
			}
		}
		return null;
	}
	
	public static MethodHandle mhc(Class<?> on) {
		Constructor<?> ct = on.getDeclaredConstructors()[0];
		ct.setAccessible(true);
		return sneakily(() -> MethodHandles.lookup().unreflectConstructor(ct));
	}
	
	public static byte[] readStream(InputStream inStream) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buf = new byte[2048];
		int read;
		while((read = inStream.read(buf)) != 0) {
			os.write(buf, 0, read);
		}
		return os.toByteArray();
	}
	
	public static DataObject readJson(InputStream inStream) {
		JsonObject obj = GSON.fromJson(new InputStreamReader(inStream), JsonObject.class);
		return (DataObject) dataFromJson(obj);
	}
	
	public static Set<Path> getDirectoryEntries(Path p) throws IOException {
		try(DirectoryStream<Path> ds = Files.newDirectoryStream(p)) {
			Set<Path> paths = new HashSet<>();
			ds.forEach(paths::add);
			return paths;
		}
	}
	
	public static DataEntry dataFromJson(JsonElement json) {
		if(json.isJsonNull()) {
			return null;
		} else if(json.isJsonPrimitive()) {
			JsonPrimitive p = json.getAsJsonPrimitive();
			if(p.isBoolean()) {
				return DataBoolean.of(p.getAsBoolean());
			} else if(p.isString()) {
				return DataString.of(p.getAsString());
			} else if(p.isNumber()) {
				int i = p.getAsInt();
				double d = p.getAsDouble();
				if(((double) i) != d) {
					return DataNumeric.of(d);
				} else {
					return DataInt.of(i);
				}
			} else {
				throw new RuntimeException("unhandled json primitive with class " + p.getClass().getName());
			}
		} else if(json.isJsonObject()) {
			Map<String, DataEntry> map = new HashMap<>();
			for(Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
				map.put(entry.getKey(), dataFromJson(entry.getValue()));
			}
			return DataObject.of(map);
		} else if(json.isJsonArray()) {
			List<DataEntry> entries = new ArrayList<>();
			for(JsonElement e : json.getAsJsonArray()) {
				entries.add(dataFromJson(e));
			}
			return DataList.of(entries.toArray(new DataEntry[entries.size()]));
		} else {
			throw new RuntimeException("unhandled json element with class " + json.getClass().getName());
		}
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
	
	public interface ExcSupplier<T, E extends Throwable> {
		T get() throws E;
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
