package skytheory.lib.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import skytheory.lib.SkyTheoryLib;

public class SkyTheoryLibLogger {

	private static final SkyTheoryLibLogger INSTANCE = new SkyTheoryLibLogger();
	private static final Logger LOGGER = LogManager.getLogger(SkyTheoryLib.MOD_NAME);

	private SkyTheoryLibLogger() {
	}

	public static SkyTheoryLibLogger getLogger() {
		return INSTANCE;
	}

	public void fatal(Object message) {
		LOGGER.fatal(message);
	}

	public void fatal(Throwable t) {
		LOGGER.fatal(t.getMessage(), t);
		t.printStackTrace();
	}

	public void error(Object message) {
		LOGGER.error(message);
	}

	public void error(Throwable t) {
		LOGGER.error(t.getMessage(), t);
		t.printStackTrace();
	}

	public void warn(Object message) {
		LOGGER.warn(message);
	}

	public void warn(Throwable t) {
		LOGGER.warn(t.getMessage(), t);
	}

	public void info(Object message) {
		LOGGER.info(message);
	}

	public void info(Throwable t) {
		LOGGER.info(t.getMessage(), t);
	}

	public void debug(Object message) {
		LOGGER.debug(message);
	}

	public void debug(Throwable t) {
		LOGGER.debug(t.getMessage(), t);
	}

	public void trace(Object message) {
		LOGGER.trace(message);
	}

	public void trace(Throwable t) {
		LOGGER.trace(t.getMessage(), t);
	}
}
