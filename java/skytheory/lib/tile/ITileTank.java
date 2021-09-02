package skytheory.lib.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import skytheory.lib.capability.fluidhandler.IFluidHandlerChangedListener;
import skytheory.lib.network.tile.TileSync;

public interface ITileTank extends IFluidHandlerChangedListener {

	public ICapabilityProvider createFluidProvider();
	public default void onFluidHandlerChanged(IFluidHandler handler) {
		TileEntity tile = (TileEntity) this;
		if (tile.hasWorld() && !tile.getWorld().isRemote) {
			tile.markDirty();
			TileSync.sendToClient(tile, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
		}
	}
}
