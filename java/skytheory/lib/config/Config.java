package skytheory.lib.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import skytheory.lib.SkyTheoryLib;

public class Config {

	private static final String DESC_WRENCH_LOG = "Logging wrench categories in console";
	private static final String DESC_DEBUG_TIPS = "Show debug tips in waila";

	public static boolean log_wrench;
	public static boolean debug_tips;

	public static List<IConfigElement> getConfigElements() {
		List<IConfigElement> elements = new ArrayList<>();
		Configuration config = SkyTheoryLib.proxy.config;
		elements.addAll(new ConfigElement(config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());
		return elements;
	}

	public static void readConfig() {
		Configuration config = SkyTheoryLib.proxy.config;
		try {
			config.load();
			initConfig(config);
		} catch (Exception e) {
			SkyTheoryLib.LOGGER.error(e);
		} finally {
			if (config.hasChanged()) {
				config.save();
			}
		}
	}

	public static void initConfig(Configuration cfg) {
		log_wrench = cfg.getBoolean("LogWrenchInit", Configuration.CATEGORY_GENERAL, false, DESC_WRENCH_LOG);
		debug_tips = cfg.getBoolean("WailaDebugTips", Configuration.CATEGORY_GENERAL, false, DESC_DEBUG_TIPS);
	}
}
