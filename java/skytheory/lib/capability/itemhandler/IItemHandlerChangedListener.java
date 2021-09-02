package skytheory.lib.capability.itemhandler;

import net.minecraftforge.items.IItemHandler;

public interface IItemHandlerChangedListener {
	public void onItemHandlerChanged(IItemHandler handler, int slot);
}
