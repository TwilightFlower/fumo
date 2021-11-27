/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.core.api;

import java.util.Objects;

public final class FumoIdentifier {
	private final String namespace, path;
	private final int hashCode;
	
	public FumoIdentifier(String namespace, String path) {
		Objects.requireNonNull(namespace, "Identifier namespace must not be null");
		Objects.requireNonNull(path, "Identifier path must not be null");
		this.namespace = namespace;
		this.path = path;
		this.hashCode = (namespace.hashCode() * 29) ^ path.hashCode(); 
	}
	
	public static FumoIdentifier fromString(String str) {
		String[] spl = str.split(":", 2);
		if(spl.length < 2) {
			throw new IllegalArgumentException(String.format("String \"%s\" is not a valid Identifier", str));
		}
		return new FumoIdentifier(spl[0], spl[1]);
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof FumoIdentifier)) return false;
		FumoIdentifier o = (FumoIdentifier) other;
		return o.hashCode == hashCode && o.namespace.equals(namespace) & o.path.equals(path);
	}
	
	@Override
	public String toString() {
		return namespace + ":" + path;
	}
}
