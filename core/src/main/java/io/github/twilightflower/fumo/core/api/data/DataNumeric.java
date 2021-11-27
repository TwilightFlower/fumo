/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof DataNumeric)) return false;
		DataNumeric o = (DataNumeric) other;
		return o.val == val;
	}
	
	@Override
	public int hashCode() {
		return Double.hashCode(val);
	}
	
	@Override
	public String toString() {
		return Double.toString(val);
	}
}
