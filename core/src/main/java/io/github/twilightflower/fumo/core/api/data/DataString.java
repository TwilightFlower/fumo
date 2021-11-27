/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.core.api.data;

public class DataString extends DataEntry {
	private final String val;
	DataString(String val) {
		this.val = val;
	}
	
	public static DataString of(String s) {
		return new DataString(s);
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
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof DataString)) return false;
		DataString o = (DataString) other;
		return o.val.equals(val);
	}
	
	@Override
	public int hashCode() {
		return val.hashCode();
	}
	
	@Override
	public String toString() {
		return val;
	}
}
