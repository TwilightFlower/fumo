/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.core.impl.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KOTHMap<K, V> implements Map<K, V> {
	private final Comparator<V> comparator;
	private final HashMap<K, V> underlying = new HashMap<>();
	
	public KOTHMap(Comparator<V> comparator) {
		this.comparator = comparator;
	}

	@Override
	public int size() {
		return underlying.size();
	}

	@Override
	public boolean isEmpty() {
		return underlying.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return underlying.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return underlying.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return underlying.get(key);
	}
	
	@Override
	public V put(K key, V value) {
		V present = get(key);
		if(present != null) {
			if(comparator.compare(value, present) > 0) {
				return underlying.put(key, value);
			} else {
				return present;
			}
		} else {
			return underlying.put(key, value);
		}
	}

	@Override
	public V remove(Object key) {
		return underlying.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for(Entry<? extends K, ? extends V> e : m.entrySet()) {
			put(e.getKey(), e.getValue());
		}
	}

	@Override
	public void clear() {
		underlying.clear();
	}

	@Override
	public Set<K> keySet() {
		return underlying.keySet();
	}

	@Override
	public Collection<V> values() {
		return underlying.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return underlying.entrySet();
	}
}
