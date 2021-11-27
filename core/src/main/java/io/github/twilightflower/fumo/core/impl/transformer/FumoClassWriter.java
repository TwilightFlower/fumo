package io.github.twilightflower.fumo.core.impl.transformer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class FumoClassWriter extends ClassWriter {
	private final TransformCache cache;
	private final Map<String, InheritanceNode> inheritanceCache = new HashMap<>();
	private final ClassLoader parentLoader;
	public FumoClassWriter(TransformCache cache, ClassLoader parentLoader, int flags) {
		super(flags);
		this.cache = cache;
		this.parentLoader = parentLoader;
	}
	
	protected String getCommonSuperClass(String type1, String type2) {
		try {
			InheritanceNode node1 = nodeOf(type1);
			InheritanceNode node2 = nodeOf(type2);
			return node1.commonSuperclass(node2).clazz;
		} catch(ClassNotFoundException e) {
			throw new RuntimeException(String.format("Could not find class while computing common superclass for %s and %s", type1, type2), e);
		}
	}
	
	private InheritanceNode nodeOf(String clazz) throws ClassNotFoundException {
		InheritanceNode node = inheritanceCache.get(clazz);
		if(node == null) {
			node = computeNode(clazz);
			inheritanceCache.put(clazz, node);
		}
		return node;
	}
	
	private InheritanceNode computeNode(String clazz) throws ClassNotFoundException {
		if(clazz.equals("java/lang/Object")) {
			return InheritanceNode.OBJECT_NODE;
		}
		ClassNode classNode = cache.maybeGetTransformedClass(clazz);
		if(classNode != null) {
			Set<InheritanceNode> parents = new HashSet<>();
			InheritanceNode mainParent = nodeOf(classNode.superName);
			parents.add(mainParent);
			for(String iface : classNode.interfaces) {
				parents.add(nodeOf(iface));
			}
			return new InheritanceNode(clazz, parents, (classNode.access & Opcodes.ACC_INTERFACE) != 0, mainParent);
		} else {
			Class<?> foundClass = Class.forName(clazz.replace('/', '.'), true, parentLoader);
			Set<InheritanceNode> parents = new HashSet<>();
			InheritanceNode mainParent = nodeOf(foundClass.getSuperclass().getName().replace('.', '/'));
			parents.add(mainParent);
			for(Class<?> iface : foundClass.getInterfaces()) {
				parents.add(nodeOf(iface.getName().replace('.', '/')));
			}
			return new InheritanceNode(clazz, parents, foundClass.isInterface(), mainParent);
		}
	}
	
	private static class InheritanceNode {
		static final InheritanceNode OBJECT_NODE = new InheritanceNode("java/lang/Object", new HashSet<>(), false, null);
		
		final InheritanceNode mainParent;
		final Set<InheritanceNode> parents;
		final String clazz;
		final boolean iface;
		InheritanceNode(String clazz, Set<InheritanceNode> parents, boolean iface, InheritanceNode mainParent) {
			this.clazz = clazz;
			this.parents = parents;
			this.iface = iface;
			this.mainParent = mainParent;
		}
		
		public boolean equals(Object other) {
			if(!(other instanceof InheritanceNode)) return false;
			InheritanceNode o = (InheritanceNode) other;
			return clazz.equals(o.clazz);
		}
		
		public boolean isSubclassOf(InheritanceNode other) {
			if(equals(other)) {
				return true;
			} else {
				for(InheritanceNode parent : parents) {
					if(parent.isSubclassOf(other)) {
						return true;
					}
				}
				return false;
			}
		}
		
		public boolean isSuperclassOf(InheritanceNode other) {
			return other.isSubclassOf(this);
		}
		
		public InheritanceNode commonSuperclass(InheritanceNode other) {
			if(equals(OBJECT_NODE)) {
				return this;
			}
			if(other.equals(OBJECT_NODE)) {
				return other;
			}
			
			if(isSuperclassOf(other)) {
				return this;
			}
			if(isSubclassOf(other)) {
				return other;
			}
			
			if(iface || other.iface) {
				return OBJECT_NODE;
			}
			
			if(mainParent != null) {
				return mainParent.commonSuperclass(other);
			}
			
			return OBJECT_NODE;
		}
	}
}
