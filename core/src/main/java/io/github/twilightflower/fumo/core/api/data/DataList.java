package io.github.twilightflower.fumo.core.api.data;

import java.util.Iterator;

public class DataList extends DataEntry implements Iterable<DataEntry> {
	private final DataEntry[] val;
	DataList(DataEntry[] val) {
		this.val = val;
	}
	
	public static DataList of(DataEntry[] val) {
		return new DataList(val.clone());
	}
	
	@Override
	public boolean isList() {
		return true;
	}

	@Override
	public Iterator<DataEntry> iterator() {
		return new Iter();
	}
	
	private class Iter implements Iterator<DataEntry> {
		int pos = 0;

		@Override
		public boolean hasNext() {
			return pos < val.length;
		}

		@Override
		public DataEntry next() {
			return val[pos++];
		}
	}
}
