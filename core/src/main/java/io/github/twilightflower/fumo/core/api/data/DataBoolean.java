/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.core.api.data;

public class DataBoolean extends DataEntry {
	public static final DataBoolean TRUE = new DataBoolean(true);
	public static final DataBoolean FALSE = new DataBoolean(false);
	
	private final boolean val;
	DataBoolean(boolean val) {
		this.val = val;
	}
	
	public static DataBoolean of(boolean b) {
		if(b) {
			return TRUE;
		} else {
			return FALSE;
		}
	}
	
	public boolean getValue() {
		return val;
	}
	
	@Override
	public boolean isBoolean() {
		return true;
	}
	
	@Override
	public DataBoolean asBoolean() {
		return this;
	}
	
	// No need to override hashCode or equals -- there are only two instances, and they have differing values
	@Override
	public String toString() {
		if(val) {
			return "true";
		} else {
			return "false";
		}
	}
}
