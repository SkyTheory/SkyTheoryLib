package skytheory.lib.capability.fluidhandler;

import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import skytheory.lib.capability.DataProviderSided;
import skytheory.lib.tile.ISidedTile;

/**
 * ISidedTileを継承させたTileEntityに実装可能なFluidHandler<br>
 * 面ごとにどのタンクに対応させられるかを設定できる
 * @author SkyTheory
 *
 */
public class FluidProviderSided extends DataProviderSided<IFluidHandler> {

	public FluidProviderSided(ISidedTile tile, IFluidHandler... tanks) {
		super(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, tile, MultiFluidHandlerModifiable::new, new MultiFluidHandlerSerializable(tanks));
	}

}
