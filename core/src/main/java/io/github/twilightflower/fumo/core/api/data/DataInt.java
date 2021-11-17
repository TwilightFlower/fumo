package io.github.twilightflower.fumo.core.api.data;

public class DataInt extends DataNumeric {
	private final int val;
	
	DataInt(int val) {
		super(val);
		this.val = val;
	}
	
	public static DataInt of(int val) {
		return new DataInt(val);
	}
	
	@Override
	public boolean isInt() {
		return true;
	}
	
	@Override
	public DataInt asInt() {
		return this;
	}
	
	public int getIntValue() {
		return val;
	}
	
	@Override
	public int hashCode() {
		return val;
	}
	
	@Override
	public String toString() {
		return Integer.toString(val);
	}
}
