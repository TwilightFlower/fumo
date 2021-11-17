package io.github.twilightflower.fumo.core.api.data;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class DataList extends DataEntry implements List<DataEntry> {
	private final DataEntry[] val;
	DataList(DataEntry[] val) {
		this.val = val;
	}
	
	private int hashCode;
	private boolean hashed;
	
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
	
	private class Iter implements ListIterator<DataEntry> {
		int pos = 0;

		@Override
		public boolean hasNext() {
			return pos < size();
		}

		@Override
		public DataEntry next() {
			return get(pos++);
		}

		@Override
		public boolean hasPrevious() {
			return pos > 0;
		}

		@Override
		public DataEntry previous() {
			return get(--pos);
		}

		@Override
		public int nextIndex() {
			return pos;
		}

		@Override
		public int previousIndex() {
			return pos - 1;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(DataEntry e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(DataEntry e) {
			throw new UnsupportedOperationException();
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof DataList)) return false;
		DataList o = (DataList) other;
		if(o.hashed && hashed && o.hashCode != hashCode) return false; // minor optimization.
		return Arrays.equals(val, o.val);
	}
	
	@Override
	public int hashCode() {
		if(!hashed) {
			hashCode = Arrays.hashCode(val);
			hashed = true;
		}
		return hashCode;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(val);
	}

	@Override
	public int size() {
		return val.length;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	@Override
	public DataEntry[] toArray() {
		return val.clone();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if(a.length < size()) {
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), val.length);
		}
		System.arraycopy(val, 0, a, 0, size());
		return a;
	}

	@Override
	public boolean add(DataEntry e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for(Object o : c) {
			if(!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends DataEntry> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends DataEntry> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public DataEntry get(int index) {
		return val[index];
	}

	@Override
	public DataEntry set(int index, DataEntry element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, DataEntry element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public DataEntry remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		if(!(o instanceof DataEntry)) return -1;
		for(int i = 0; i < size(); i++) {
			if(Objects.equals(o, get(i))) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		if(!(o instanceof DataEntry)) return -1;
		for(int i = size() - 1; i >= 0; i--) {
			if(Objects.equals(o, get(i))) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public ListIterator<DataEntry> listIterator() {
		return new Iter();
	}

	@Override
	public ListIterator<DataEntry> listIterator(int index) {
		Iter i = new Iter();
		i.pos = index;
		return i;
	}

	// yes, this isn't how subList is supposed to work, however, since this list is unmodifiable, it works.
	@Override
	public List<DataEntry> subList(int fromIndex, int toIndex) {
		DataEntry[] newVal = new DataEntry[toIndex - fromIndex];
		System.arraycopy(val, fromIndex, newVal, 0, newVal.length);
		return new DataList(newVal);
	}
}
