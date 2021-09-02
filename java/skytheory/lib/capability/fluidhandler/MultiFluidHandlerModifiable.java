package skytheory.lib.capability.fluidhandler;

import java.util.Collection;

import net.minecraftforge.fluids.capability.IFluidHandler;
import skytheory.lib.capability.IModifiableHandler;

/**
 * 複数のFluidTankを結合したFluidHandler<br>
 * タンクの動的な追加・除去に対応する<br>
 * @author SkyTheory
 *
 */
public class MultiFluidHandlerModifiable extends MultiFluidHandler implements IModifiableHandler<IFluidHandler> {

	@Override
	public void addData(IFluidHandler tank) {
		this.tanks.add(tank);
	}

	@Override
	public void removeData(IFluidHandler tank) {
		this.tanks.remove(tank);
	}

	@Override
	public Collection<IFluidHandler> getDatas() {
		return this.tanks;
	}

}
