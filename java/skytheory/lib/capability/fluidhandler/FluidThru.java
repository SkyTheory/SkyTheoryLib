package skytheory.lib.capability.fluidhandler;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import skytheory.lib.tile.ISidedTile;
import skytheory.lib.util.EnumSide;

/**
 * 第一引数のTileEntityが受け取った液体を第二引数のSideにあるTileEntityに渡すItemHandler
 * @author SkyTheory
 *
 */
public class FluidThru implements IFluidHandler {

	public final TileEntity tile;
	public final ISidedTile sided;
	public final EnumSide side;

	public <T extends TileEntity & ISidedTile> FluidThru(T tile, EnumSide side) {
		this.tile = tile;
		this.sided = tile;
		this.side = side;
	}

	private IFluidHandler getFluidHandler() {
		EnumFacing direction = sided.getFacing(side);
		TileEntity target = tile.getWorld().getTileEntity(tile.getPos().offset(direction));
		if (target != null) {
			return target.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
		}
		return null;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		IFluidHandler handler = this.getFluidHandler();
		if (handler != null) return handler.getTankProperties();
		return new IFluidTankProperties[0];
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		IFluidHandler handler = this.getFluidHandler();
		if (handler != null) return handler.fill(resource, doFill);
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		IFluidHandler handler = this.getFluidHandler();
		if (handler != null) {
			FluidStack result = handler.drain(resource, doDrain);
			return result;
		}
		return null;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		IFluidHandler handler = this.getFluidHandler();
		if (handler != null) return handler.drain(maxDrain, doDrain);
		return null;
	}

}
