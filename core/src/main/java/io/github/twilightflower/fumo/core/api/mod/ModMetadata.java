package io.github.twilightflower.fumo.core.api.mod;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.zafarkhaja.semver.Version;
import com.github.zafarkhaja.semver.expr.Expression;

import io.github.twilightflower.fumo.core.api.data.DataObject;
import io.github.twilightflower.fumo.core.api.data.DataString;
import io.github.twilightflower.fumo.core.api.data.codec.FumoCodec;
import io.github.twilightflower.fumo.core.impl.util.FumoCodecs;
import io.github.twilightflower.fumo.core.impl.util.Util;

import static io.github.twilightflower.fumo.core.api.data.codec.FumoCodec.*;

public final class ModMetadata {
	private static final FumoCodec<DataObject, Set<ModVersionExpression>> EXPR_CODEC =
			defaultVal(
				iterateMap(
					compose(
						pair(
							identity(String.class),
							compose(
								cast(DataString.class, STRING),
								FumoCodecs.VERSION_EXPR
							)
						),
						function(ModVersionExpression::ofEntry, ModVersionExpression.class)
					),
					HashSet::new
				),
				HashSet::new
			);
	
	public static FumoCodec<DataObject, ModMetadata> codec(Path root, boolean forced) {
		return multi(
			Util.mhc(ModMetadata.class), ModMetadata.class,
			entry(
				constant("id"),
				STRING,
				DataString.class,
				true
			),
			entry(
				constant("name"),
				propogateNull(STRING),
				DataString.class,
				false
			),
			entry(
				constant("version"),
				compose(
					STRING,
					FumoCodecs.VERSION
				),
				DataString.class,
				true
			),
			identity(DataObject.class),
			constant(root),
			entry(
				constant("requires"),
				EXPR_CODEC,
				DataObject.class,
				true
			),
			entry(
				constant("conflicts"),
				EXPR_CODEC,
				DataObject.class,
				false
			),
			constant(forced)
		);
	}
	
	private final String id, name;
	private final Version version;
	private final DataObject data;
	private final Path root;
	private final Set<ModVersionExpression> requiresExprs = new HashSet<>(), conflictsExprs = new HashSet<>();
	private final boolean forced;
	
	public ModMetadata(String id, String name, Version version, DataObject data, Path root, Set<ModVersionExpression> requiresExprs, Set<ModVersionExpression> conflictsExprs, boolean forced) {
		this.id = id;
		this.name = name != null ? name : id;
		this.version = version;
		this.data = data;
		this.root = root;
		this.requiresExprs.addAll(requiresExprs);
		this.conflictsExprs.addAll(conflictsExprs);
		this.forced = forced;
	}
	
	public static ModMetadata parse(InputStream json, Path root, boolean forced) {
		return codec(root, forced).decode(Util.readJson(json));
	}
	
	public static ModMetadata parse(Path root, boolean forced) throws IOException {
		try(InputStream in = new BufferedInputStream(Files.newInputStream(root.resolve("fumo.mod.json")))) {
			return parse(in, root, forced);
		}
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Version getVersion() {
		return version;
	}
	
	public DataObject getData() {
		return data;
	}
	
	public Path getRoot() {
		return root;
	}
	
	public boolean isForced() {
		return forced;
	}
	
	public Set<ModVersionExpression> getRequiresExprs() {
		return Collections.unmodifiableSet(requiresExprs);
	}
	
	public Set<ModVersionExpression> getConflictsExprs() {
		return Collections.unmodifiableSet(conflictsExprs);
	}
	
	public static class ModVersionExpression {
		private final Expression expr;
		private final String modId;
		
		public ModVersionExpression(Expression expr, String modId) {
			this.expr = expr;
			this.modId = modId;
		}
		
		public static ModVersionExpression ofEntry(Map.Entry<String, Expression> of) {
			return new ModVersionExpression(of.getValue(), of.getKey());
		}
		
		public String getModId() {
			return modId;
		}
		
		public Expression getExpression() {
			return expr;
		}
		
		public boolean equals(Object other) {
			if(!(other instanceof ModVersionExpression)) return false;
			ModVersionExpression o = (ModVersionExpression) other;
			return o.expr.equals(expr) && o.modId.equals(modId);
		}
		
		public int hashCode() {
			return expr.hashCode() * 31 + modId.hashCode();
		}
	}
	
	public boolean equals(Object other) {
		if(!(other instanceof ModMetadata)) return false;
		ModMetadata o = (ModMetadata) other;
		return o.id.equals(id) && o.version.equals(version); // this is generally good enough
	}
	
	public int hashCode() {
		return id.hashCode() * 31 + version.hashCode();
	}
}
