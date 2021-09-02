package skytheory.lib.capability.itemhandler;

import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import skytheory.lib.capability.DataProviderSided;
import skytheory.lib.tile.ISidedTile;

public class ItemProviderSided extends DataProviderSided<IItemHandler> {

	public ItemProviderSided(ISidedTile tile, IItemHandler... handlers) {
		super(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, tile, MultiItemHandlerModifiable::new, new MultiItemHandlerSerializable(handlers));
	}

}
