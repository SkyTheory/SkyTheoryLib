package skytheory.lib.plugin.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;

public interface IWailaTipBlock {

	public void getWailaTips(ItemStack stack, List<String> tips, IWailaDataAccessor accessor);
}
