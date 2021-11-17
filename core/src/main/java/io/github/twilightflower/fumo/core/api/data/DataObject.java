package io.github.twilightflower.fumo.core.api.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataObject extends DataEntry implements Map<String, DataEntry> {
	private final Map<String, DataEntry> data;
	
	private boolean hashed;
	private int hashCode;
	
	DataObject(Map<String, DataEntry> data) {
		Map<String, DataEntry> copy = new HashMap<>();
		copy.putAll(data);
		this.data = Collections.unmodifiableMap(copy);
	}
	
	public static DataObject of(Map<String, DataEntry> data) {
		return new DataObject(data);
	}
	
	@Override
	public boolean isObject() {
		return true;
	}
	
	@Override
	public DataObject asObject() {
		return this;
	}
	
	@Override
	public DataEntry get(Object key) {
		return data.get(key);
	}
	
	@Override
	public boolean containsKey(Object key) {
		return data.containsKey(key);
	}
	
	@Override
	public Set<String> keySet() {
		return Collections.unmodifiableSet(data.keySet());
	}
	
	@Override
	public Collection<DataEntry> values() {
		return Collections.unmodifiableCollection(data.values());
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public boolean containsValue(Object value) {
		return data.containsKey(value);
	}

	@Override
	public DataEntry put(String key, DataEntry value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public DataEntry remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends DataEntry> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<String, DataEntry>> entrySet() {
		return data.entrySet();
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof DataObject)) return false;
		DataObject o = (DataObject) other;
		if(o.hashed && hashed && o.hashCode != hashCode) return false;
		return o.data.equals(data);
	}
	
	@Override
	public int hashCode() {
		if(!hashed) {
			hashCode = data.hashCode();
			hashed = true;
		}
		return hashCode;
	}
}
