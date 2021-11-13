package io.github.twilightflower.fumo.core.api.data;

public class DataString extends DataEntry {
	private final String val;
	public DataString(String val) {
		this.val = val;
	}
	
	@Override
	public boolean isString() {
		return true;
	}
	
	@Override
	public DataString asString() {
		return this;
	}
	
	public String getValue() {
		return val;
	}
}
