package io.github.twilightflower.fumo.core.api;

import java.util.Objects;

public class Identifier {
	private final String namespace, path;
	private final int hashCode;
	
	public Identifier(String namespace, String path) {
		Objects.requireNonNull(namespace, "Identifier namespace must not be null");
		Objects.requireNonNull(path, "Identifier path must not be null");
		this.namespace = namespace;
		this.path = path;
		this.hashCode = (namespace.hashCode() * 29) ^ path.hashCode(); 
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Identifier)) return false;
		Identifier o = (Identifier) other;
		return o.hashCode == hashCode && o.namespace.equals(namespace) & o.path.equals(path);
	}
}
