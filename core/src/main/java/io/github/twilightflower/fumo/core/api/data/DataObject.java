package io.github.twilightflower.fumo.core.api.data;

import java.util.HashMap;
import java.util.Map;

public class DataObject extends DataEntry {
	private final Map<String, DataEntry> data = new HashMap<>();
	
	DataObject(Map<String, DataEntry> data) {
		this.data.putAll(data);
	}
	
	public DataObject of(Map<String, DataEntry> data) {
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
	
	public DataEntry getEntry(String key) {
		return data.get(key);
	}
	
	public boolean containsEntry(String key) {
		return data.containsKey(key);
	}
}
