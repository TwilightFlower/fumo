package io.github.twilightflower.fumo.core.api;

public enum LoaderState {
	INIT,
	LOADING_PLUGINS,
	LOADED_PLUGINS,
	LOADING_MODS,
	LOADED_MODS,
	REGISTERING_TRANSFORMERS,
	PRELAUNCH,
	LAUNCHED;
	
	public boolean isAfter(LoaderState other) {
		return this.ordinal() > other.ordinal();
	}
	
	public void ensureIsAfter(LoaderState other) {
		if(!isAfter(other)) {
			throw new IllegalStateException(String.format("Illegal state %s (must be a state coming after %s)", name(), other.name()));
		}
	}
	
	public boolean isBefore(LoaderState other) {
		return this.ordinal() < other.ordinal();
	}
	
	public void ensureIsBefore(LoaderState other) {
		if(!isBefore(other)) {
			throw new IllegalStateException(String.format("Illegal state %s (must be a state coming before %s)", name(), other.name()));
		}
	}
	
	public boolean accessPlugins() {
		return isAfter(LOADING_PLUGINS);
	}
	
	public void ensureAccessPlugins() {
		if(!accessPlugins()) {
			throw new IllegalStateException(String.format("Cannot access plugins in state %s", name()));
		}
	}
	
	public boolean accessTransformers() {
		return isAfter(REGISTERING_TRANSFORMERS);
	}
	
	public void ensureAccessTransformers() {
		if(!accessTransformers()) {
			throw new IllegalStateException(String.format("Cannot access transformers in state %s", name()));
		}
	}
	
	public boolean accessMods() {
		return isAfter(LOADING_MODS);
	}
	
	public void ensureAccessMods() {
		if(!accessMods()) {
			throw new IllegalStateException(String.format("Cannot access mods in state %s", name()));
		}
	}
}
