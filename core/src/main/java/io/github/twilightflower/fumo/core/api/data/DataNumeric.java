package io.github.twilightflower.fumo.core.api.data;

public class DataNumeric extends DataEntry {
	private final double val;
	
	DataNumeric(double val) {
		this.val = val;
	}
	
	public static DataNumeric of(double val) {
		return new DataNumeric(val);
	}
	
	@Override
	public boolean isNumeric() {
		return true;
	}
	
	@Override
	public DataNumeric asNumeric() {
		return this;
	}
	
	public double getValue() {
		return val;
	}
}
