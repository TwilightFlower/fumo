package io.github.twilightflower.fumo.core.api.data;

public class DataEntry {
	DataEntry() {
		
	}
	
	public boolean isObject() {
		return false;
	}
	
	public boolean isNumeric() {
		return false;
	}
	
	public boolean isInt() {
		return false;
	}
	
	public boolean isBoolean() {
		return false;
	}
	
	public boolean isList() {
		return false;
	}
	
	public boolean isString() {
		return false;
	}
	
	public DataObject asObject() {
		throw new UnsupportedOperationException();
	}
	
	public DataInt asInt() {
		throw new UnsupportedOperationException();
	}
	
	public DataString asString() {
		throw new UnsupportedOperationException();
	}
	
	public DataNumeric asNumeric() {
		throw new UnsupportedOperationException();
	}
	
	public DataList asList() {
		throw new UnsupportedOperationException();
	}
}
