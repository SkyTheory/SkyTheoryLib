package skytheory.lib.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import skytheory.lib.SkyTheoryLib;

public class ConfigGuiScreen extends GuiConfig {
	public ConfigGuiScreen(GuiScreen parent) {
		super(parent, Config.getConfigElements(), SkyTheoryLib.MOD_ID, false, false, I18n.format("stlib.tip.config"));
	}

	@Override
	public void onGuiClosed() {
		Configuration config = SkyTheoryLib.proxy.config;
		if (config.hasChanged()) {
			config.save();
			Config.initConfig(config);
		}
		super.onGuiClosed();
	}
}
