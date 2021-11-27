package io.github.twilightflower.fumo.core.impl.transformer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import io.github.twilightflower.fumo.core.api.FumoIdentifier;
import io.github.twilightflower.fumo.core.api.transformer.ClassTransformer;
import io.github.twilightflower.fumo.core.api.transformer.TransformerRegistry;

public class TransformerGraphBuilder implements TransformerRegistry {
	private final Set<UnresolvedNode> nodes = new HashSet<>();
	
	@Override
	public void registerTransformer(FumoIdentifier id, ClassTransformer transformer, Set<FumoIdentifier> runBefore, Set<FumoIdentifier> runAfter) {
		if(!nodes.add(new UnresolvedNode(id, transformer, runBefore, runAfter))) {
			throw new IllegalArgumentException(String.format("Transformer with ID %s already exists!", id));
		}
	}
	
	public List<ClassTransformer> resolve() {
		Map<FumoIdentifier, Node> resolvedNodes = new HashMap<>();
		for(UnresolvedNode unode : nodes) {
			resolvedNodes.put(unode.id, new Node(unode.transformer, unode.id));
		}
		// build the graph of nodes. Parents run first.
		for(UnresolvedNode unode : nodes) {
			Node resolved = resolvedNodes.get(unode.id);
			for(FumoIdentifier id : unode.runBefore) {
				Node link = resolvedNodes.get(id);
				if(link != null) {
					resolved.children.add(link);
					link.parents.add(resolved);
				}
			}
			for(FumoIdentifier id : unode.runAfter) {
				Node link = resolvedNodes.get(id);
				if(link != null) {
					resolved.parents.add(link);
					link.children.add(resolved);
				}
			}
		}
		
		// Find the head nodes
		Set<Node> headNodes = new HashSet<>();
		for(Node node : resolvedNodes.values()) {
			if(node.parents.isEmpty()) {
				headNodes.add(node);
			}
		}
		
		Set<Node> visited = new HashSet<>();
		// Validation: walking the graph downwards from each head node, and erroring if a node is encountered multiple times.
		// (the set is technically unnecessary, as the stack holds the same data, but it's faster (O(1) rather than O(n))
		for(Node n : headNodes) {
			Deque<VisitStackEntry> stack = new ArrayDeque<>();
			Set<Node> currentlyVisiting = new HashSet<>();
			stack.push(new VisitStackEntry(n));
			currentlyVisiting.add(n);
			visited.add(n);
			while(!stack.isEmpty()) {
				VisitStackEntry vse = stack.peek();
				if(vse.children.isEmpty()) {
					stack.poll(); // pop it off the stack
					currentlyVisiting.remove(vse.node);
				} else {
					Node next = vse.children.poll();
					visited.add(next);
					stack.push(new VisitStackEntry(next));
					if(!currentlyVisiting.add(next)) {
						throw new RuntimeException(String.format("circular transformer graph (%s circles back to %s)", vse.node.id, next.id));
					}
				}
			}
		}
		
		// Validation step 2: we might have missed a node, which is the case if there's a fully circular chain with no head. Check the counts.
		// This step is only necessary because we only check from the head nodes as an optimization.
		if(visited.size() != resolvedNodes.size()) {
			throw new RuntimeException("Un-found circular transformer dependency exists.");
		}
		
		// The graph is non-circular! Yay! However, walking a graph to transform every class isn't exactly fast.
		// Let's flatten things out some.
		Set<Node> evald = new HashSet<>();
		List<ClassTransformer> addTo = new ArrayList<>();
		for(Node n : headNodes) {
			flatten(n, evald, addTo);
		}
		
		return addTo;
	}
	
	private void flatten(Node n, Set<Node> evald, List<ClassTransformer> addTo) {
		Deque<VisitStackEntry> stack = new ArrayDeque<>();
		stack.push(new VisitStackEntry(n));
		while(!stack.isEmpty()) {
			VisitStackEntry node = stack.peek();
			if(evald.contains(node.node)) { // nodes can get pushed twice under certain circumstances
				stack.pop();
			} else {
				if(!node.children.isEmpty()) {
					stack.push(new VisitStackEntry(node.children.pop()));
				} else {
					evald.add(node.node);
					addTo.add(node.node.transformer);
				}
			}
		}
	}
		
	private static class UnresolvedNode {
		private final Set<FumoIdentifier> runBefore, runAfter;
		private final FumoIdentifier id;
		private final ClassTransformer transformer;
		
		UnresolvedNode(FumoIdentifier id, ClassTransformer transformer, Set<FumoIdentifier> runBefore, Set<FumoIdentifier> runAfter) {
			this.id = id;
			this.transformer = transformer;
			this.runAfter = runAfter;
			this.runBefore = runBefore;
		}
		
		public int hashCode() {
			return id.hashCode();
	}
		
		public boolean equals(Object other) {
			if(!(other instanceof UnresolvedNode)) return false;
			UnresolvedNode n = (UnresolvedNode) other;
			return n.id.equals(id);
		}
	}
	
	private static class VisitStackEntry {
		private final Node node;
		private final Deque<Node> parents = new ArrayDeque<>();
		private final Deque<Node> children = new ArrayDeque<>();
		
		VisitStackEntry(Node of) {
			node = of;
			children.addAll(of.children);
			parents.addAll(of.parents);
		}
	}
	
	private static class Node {
		private final Set<Node> parents = new HashSet<>(), children = new HashSet<>();
		private final ClassTransformer transformer;
		private final FumoIdentifier id;
		
		Node(ClassTransformer transformer, FumoIdentifier id) {
			this.transformer = transformer;
			this.id = id;
		}
		
		public int hashCode() {
			return id.hashCode();
		}
		
		public boolean equals(Object other) {
			if(!(other instanceof Node)) return false;
			Node n = (Node) other;
			return n.id.equals(id);
		}
		
		public String toString() {
			return id.toString();
		}
	}
}
