package io.github.twilightflower.fumo.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.logging.Level;
import org.spongepowered.asm.logging.LoggerAdapterAbstract;

public class MixinLogger extends LoggerAdapterAbstract {
	private static final org.apache.logging.log4j.Level[] LEVELS = {
		org.apache.logging.log4j.Level.FATAL,
		org.apache.logging.log4j.Level.ERROR,
		org.apache.logging.log4j.Level.WARN,
		org.apache.logging.log4j.Level.INFO,
		org.apache.logging.log4j.Level.DEBUG,
		org.apache.logging.log4j.Level.TRACE
	};
	
	private final Logger logger;
	
	public MixinLogger(String id) {
		super(id);
		logger = LogManager.getLogger(id);
	}
	
	@Override
	public String getType() {
		return "Log4J (via fumo)";
	}
	
	@Override
	public void catching(Level level, Throwable t) {
		logger.catching(lvl(level), t);
	}

	@Override
	public void log(Level level, String message, Object... params) {
		logger.log(lvl(level), message, params);
	}

	@Override
	public void log(Level level, String message, Throwable t) {
		logger.log(lvl(level), message, t);
	}

	@Override
	public <T extends Throwable> T throwing(T t) {
		logger.throwing(t);
		return t;
	}
		
	private org.apache.logging.log4j.Level lvl(Level from) {
		return LEVELS[from.ordinal()];
	}
}