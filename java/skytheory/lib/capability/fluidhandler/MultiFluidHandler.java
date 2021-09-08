package skytheory.lib.capability.fluidhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.Validate;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate;
import net.minecraftforge.fluids.capability.templates.FluidHandlerFluidMap;

/**
 * 複数のFluidTankを結合したFluidHandler<br>
 * 基本的に一種の液体を複数タンクに入れることはしない<br>
 * そういう用途なら{@link FluidHandlerConcatenate}を用いること<br>
 * {@link FluidHandlerFluidMap}との違いは、動的にタンクを変動させられること
 * @author SkyTheory
 *
 */
public class MultiFluidHandler implements IFluidHandler {

	protected final List<IFluidHandler> tanks;

	// tryFluidTransferによるバグの対策
	private FluidStack specific;

	public MultiFluidHandler(IFluidHandler... tanks) {
		this.tanks = new ArrayList<>(Arrays.asList(Validate.noNullElements(tanks)));
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		// 覚書：modによってはProperties[0]しか参照しない場合もあるのでnullLastにしておく
		// 致命的な不具合が出た場合、ソート処理を外すこと
		return tanks.stream()
				.map(tank -> tank.getTankProperties())
				.flatMap(Arrays::stream)
				.sorted(Comparator.comparing(prop -> prop.getContents() == null))
				.toArray(IFluidTankProperties[]::new);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (resource == null) return 0;
		for (IFluidHandler tank : this.tanks) {
			for (IFluidTankProperties prop : tank.getTankProperties()) {
				if (resource.isFluidEqual(prop.getContents())) {
					return tank.fill(resource, doFill);
				}
			}
		}
		for (IFluidHandler tank : this.tanks) {
			int amount = tank.fill(resource, doFill);
			if (amount > 0) return amount;
		}
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		FluidStack result = drainInternal(resource, doDrain);
		if (!doDrain) {
			this.specific = result;
		}
		return result;
	}

	private FluidStack drainInternal(FluidStack resource, boolean doDrain) {
		if (resource == null) return null;
		FluidStack result = null;
		FluidStack next = resource.copy();
		for (IFluidHandler tank : this.tanks) {
			if (next.amount <= 0) break;
			FluidStack drained = tank.drain(next.copy(), false);
			if (drained != null && drained.isFluidEqual(next.copy())) {
				drained = tank.drain(next, doDrain).copy();
				if (result == null) {
					result = drained;
					next.amount -= drained.amount;
				} else {
					result.amount += drained.amount;
					next.amount -= drained.amount;
				}
			}
		}
		return result;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		// 覚書：1.12.2版のForgeではtryFluidTransferを利用するとバグるため、対策にフィルタを挟む
		// 少なくとも1.16.5では大丈夫になっていることを確認済み
		if (this.specific == null) {
			FluidStack result = null;
			int nextAmount = maxDrain;
			for (IFluidHandler tank : this.tanks) {
				if (nextAmount <= 0) break;
				if (result == null) {
					FluidStack drained = tank.drain(maxDrain, doDrain);
					if (drained != null) {
						result = drained.copy();
						nextAmount -= drained.amount;
					}
				} else {
					FluidStack next = result.copy();
					next.amount = nextAmount;
					FluidStack drained = tank.drain(next, doDrain);
					if (drained != null) {
						nextAmount -= drained.amount;
						result.amount += drained.amount;
					}
				}
			}
			return result;
		} else {
			this.specific = specific.copy();
			this.specific.amount = maxDrain;
			FluidStack result = this.drainInternal(specific, doDrain);
			this.specific = null;
			return result;
		}
	}

}
