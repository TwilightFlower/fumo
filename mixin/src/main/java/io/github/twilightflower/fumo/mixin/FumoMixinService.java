package io.github.twilightflower.fumo.mixin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.CompatibilityLevel;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.IClassTracker;
import org.spongepowered.asm.service.IMixinAuditTrail;
import org.spongepowered.asm.service.IMixinInternal;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.ITransformerProvider;
import org.spongepowered.asm.util.ReEntranceLock;

import io.github.twilightflower.fumo.core.api.FumoIdentifier;
import io.github.twilightflower.fumo.core.api.FumoLoader;
import io.github.twilightflower.fumo.core.api.data.DataList;
import io.github.twilightflower.fumo.core.api.data.DataObject;
import io.github.twilightflower.fumo.core.api.data.DataString;
import io.github.twilightflower.fumo.core.api.data.codec.FumoCodec;
import io.github.twilightflower.fumo.core.api.mod.ModMetadata;
import io.github.twilightflower.fumo.core.api.plugin.FumoLoaderPlugin;
import io.github.twilightflower.fumo.core.api.transformer.ClassTransformer;
import io.github.twilightflower.fumo.core.api.transformer.TransformerRegistry;
import io.github.twilightflower.fumo.core.impl.FumoLoaderImpl;
import io.github.twilightflower.fumo.mixin.spi.SideNameProvider;

import static io.github.twilightflower.fumo.core.api.data.codec.FumoCodec.*;

public class FumoMixinService implements IMixinService, IClassBytecodeProvider, IClassProvider, ClassTransformer, FumoLoaderPlugin {
	private static final FumoCodec<DataObject, List<String>> MIXIN_CONFIGS =
		entry(
			constant("mixins"),
			defaultVal(
				iterate(
					cast(DataString.class, STRING)
				),
				ArrayList::new
			),
			DataList.class,
			false
		);
	
	private final ReEntranceLock lock = new ReEntranceLock(1);
	private static FumoLoader loader;
	static IMixinTransformerFactory transformerFactory;
	static IMixinTransformer transformer;
	
	static ClassLoader gameLoader;
	static ClassLoader pluginLoader;
	
	static SideNameProvider sideNameProvider;
	
	@Override
	public void init(FumoLoader loader) {
		FumoMixinService.loader = loader;
		
		boolean found = false;
		for(SideNameProvider p : ServiceLoader.load(SideNameProvider.class, pluginLoader)) {
			if(!found) {
				sideNameProvider = p;
				found = true;
			} else {
				throw new RuntimeException(String.format("Found multiple side name providers: %s and %s", sideNameProvider.getClass().getName(), p.getClass().getName()));
			}
		}
		
		if(!found) {
			sideNameProvider = () -> "universal";
		}
	}
	
	@Override
	public void pluginsLoaded() {
		pluginLoader = loader.getPluginClassloader();
	}
	
	@Override
	public void preLaunch(ClassLoader targetLoader) {
		gameLoader = targetLoader;
		
		MixinBootstrap.init();
		MixinEnvironment.init(Phase.DEFAULT);
		
		for(ModMetadata mod : loader.getAllMods()) {
			System.out.println(mod.getId());
			List<String> configs = MIXIN_CONFIGS.decode(mod.getData());
			if(configs != null) {
				for(String config : configs) {
					System.out.println("Loaded mixin config " + config);
					Mixins.addConfiguration(config);
				}
			}
		}
		
		transformer = transformerFactory.createTransformer();
	}
	
	@Override
	public void registerTransformers(TransformerRegistry registry) {
		registry.registerTransformer(new FumoIdentifier("mixin", "mixin"), this);
	}
	
	@Override
	public String getName() {
		return "FumoMixinService";
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void prepare() { }

	@Override
	public Phase getInitialPhase() {
		return Phase.DEFAULT;
	}

	@Override
	public void offer(IMixinInternal internal) {
		if(internal instanceof IMixinTransformerFactory) {
			transformerFactory = (IMixinTransformerFactory) internal;
		}
	}

	@Override
	public void init() { }

	@Override
	public void beginPhase() { }

	@Override
	public void checkEnv(Object bootSource) { }

	@Override
	public ReEntranceLock getReEntranceLock() {
		return lock;
	}

	@Override
	public IClassProvider getClassProvider() {
		return this;
	}

	@Override
	public IClassBytecodeProvider getBytecodeProvider() {
		return this;
	}

	@Override
	public ITransformerProvider getTransformerProvider() {
		return null;
	}

	@Override
	public IClassTracker getClassTracker() {
		return null;
	}

	@Override
	public IMixinAuditTrail getAuditTrail() {
		return null;
	}

	@Override
	public Collection<String> getPlatformAgents() {
		return Collections.emptyList();
	}

	@Override
	public IContainerHandle getPrimaryContainer() {
		try {
			return new ContainerHandleURI(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Collection<IContainerHandle> getMixinContainers() {
		return Collections.emptyList();
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		return gameLoader.getResourceAsStream(name);
	}
	
	@Override
	public ILogger getLogger(String name) {
		return new MixinLogger(name);
	}

	@Override
	public String getSideName() {
		return sideNameProvider.getSideName();
	}

	@Override
	public CompatibilityLevel getMinCompatibilityLevel() {
		return null;
	}

	@Override
	public CompatibilityLevel getMaxCompatibilityLevel() {
		return null;
	}

	@Override
	public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
		return getClassNode(name, true);
	}

	@Override
	public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
		if(!runTransformers) {
			String fileName = name.replace('.', '/') + ".class";
			try(InputStream clazz = getResourceAsStream(fileName)) {
				if(clazz == null) {
					throw new ClassNotFoundException(name);
				} else {
					ClassReader reader = new ClassReader(clazz);
					ClassNode node = new ClassNode();
					reader.accept(node, 0);
					return node;
				}
			}
		} else {
			ClassNode node = getClassNode(name, false);
			return ((FumoLoaderImpl) loader).transformUntil(name, node, this);
		}
	}

	@Override
	public boolean transforms(String className) {
		return true; // TODO fix this.
	}

	@Override
	public ClassNode transform(String className, ClassNode clazz) {
		transformer.transformClass(MixinEnvironment.getCurrentEnvironment(), className, clazz);
		return clazz;
	}

	@Deprecated
	@Override
	public URL[] getClassPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		return gameLoader.loadClass(name);
	}

	@Override
	public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
		return Class.forName(name, initialize, gameLoader);
	}

	@Override
	public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
		return Class.forName(name, initialize, pluginLoader);
	}
}
