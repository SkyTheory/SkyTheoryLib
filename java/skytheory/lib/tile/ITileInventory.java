package skytheory.lib.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import skytheory.lib.capability.itemhandler.IItemHandlerChangedListener;
import skytheory.lib.network.tile.TileSync;

public interface ITileInventory extends IItemHandlerChangedListener {

	public ICapabilityProvider createInventoryProvider();
	public default void onItemHandlerChanged(IItemHandler handler, int slot) {
		TileEntity tile = (TileEntity) this;
		tile.markDirty();
		if (tile.hasWorld() && !tile.getWorld().isRemote) {
			TileSync.sendToClient(tile, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		}
	};
}
