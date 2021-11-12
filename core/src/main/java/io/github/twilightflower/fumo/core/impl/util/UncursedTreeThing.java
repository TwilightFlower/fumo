package io.github.twilightflower.fumo.core.impl.util;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class UncursedTreeThing<T> {
	private final Node<T> root = new Node<>();
	private final Comparator<T> comparator;
	
	public UncursedTreeThing(Comparator<T> comparator) {
		this.comparator = comparator;
	}
	
	public void add(T entry) {
		Node<T> n = root;
		int cmp;
		do {
			cmp = 0;
			for(T t : n.vals) {
				cmp = comparator.compare(entry, t);
				if(cmp != 0) {
					break;
				}
			}
			if(cmp < 0) {
				n = n.left();
			} else if(cmp > 0) {
				n = n.right();
			}
			
		} while(cmp != 0);
		n.vals.add(entry);
	}
	
	private static class Node<T> {
		final Set<T> vals = new HashSet<>();
		Node<T> left, right;
		
		Node<T> left() {
			if(left == null) {
				left = new Node<>();
			}
			
			return left;
		}
		
		Node<T> right() {
			if(right == null) {
				right = new Node<>();
			}
			
			return right;
		}
	}
}
