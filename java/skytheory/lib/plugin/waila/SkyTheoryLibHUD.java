package skytheory.lib.plugin.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.item.ItemStack;

@WailaPlugin
public class SkyTheoryLibHUD implements IWailaPlugin {

	@Override
	public void register(IWailaRegistrar registrar) {
		registerTips(registrar);
	}

	public static void registerTips(IWailaRegistrar registrar) {

		IWailaDataProvider block = new IWailaDataProvider() {
			@Override
			public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
				if (accessor.getBlock() instanceof IWailaTipBlock) {
					((IWailaTipBlock) accessor.getBlock()).getWailaTips(itemStack, currenttip, accessor);
				}
				return currenttip;
			}
		};

		IWailaDataProvider tile = new IWailaDataProvider() {
			@Override
			public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
				if (accessor.getTileEntity() instanceof IWailaTipTile) {
					((IWailaTipTile) accessor.getTileEntity()).getWailaTips(itemStack, currenttip, accessor);
				}
				return currenttip;
			}
		};

		registrar.registerBodyProvider(block, IWailaTipBlock.class);
		registrar.registerBodyProvider(tile, IWailaTipTile.class);
	}

}
