package skytheory.lib.capability.fluidhandler;

import java.util.function.Predicate;

import org.apache.commons.lang3.Validate;

import com.google.common.base.Predicates;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 * IFluidHandlerをWrapして搬入出の制御を追加する<br>
 * この面からは搬入専用にしたい、などという時に利用することを想定している
 * @author SkyTheory
 *
 */
public class FluidAccessor implements IFluidHandler {

	public final IFluidHandler handler;

	private Predicate<FluidStack> canFill;
	private Predicate<FluidStack> canDrain;

	public static IFluidHandler fillOnly(IFluidHandler... handler) {
		IFluidHandler wrap = new FluidAccessor(handler).setCanDrain(false);
		return wrap;
	}

	public static IFluidHandler drainOnly(IFluidHandler... handler) {
		IFluidHandler wrap = new FluidAccessor(handler).setCanFill(false);
		return wrap;
	}

	public FluidAccessor(IFluidHandler... handler) {
		Validate.notEmpty(handler);
		if (handler.length == 1) {
			this.handler = handler[0];
		} else {
			this.handler = new MultiFluidHandler(handler);
		}
	}

	public FluidAccessor setCanFill(boolean canFill) {
		if (canFill) this.canFill = null; else this.canFill = Predicates.alwaysFalse();
		return this;
	}

	public FluidAccessor setCanFill(Predicate<FluidStack> canFill) {
		this.canFill = canFill;
		return this;
	}

	public FluidAccessor setCanDrain(boolean canDrain) {
		if (canDrain) this.canDrain = null; else this.canDrain = Predicates.alwaysFalse();
		return this;
	}

	public FluidAccessor setCanDrain(Predicate<FluidStack> canDrain) {
		this.canDrain = canDrain;
		return this;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return handler.getTankProperties();
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (canFill == null || canFill.test(resource)) return handler.fill(resource, doFill);
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (canDrain == null || canDrain.test(resource)) return handler.drain(resource, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		FluidStack stack = handler.drain(maxDrain, false);
		return drain(stack, doDrain);
	}

}
