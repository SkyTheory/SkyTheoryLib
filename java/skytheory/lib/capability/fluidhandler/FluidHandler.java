package skytheory.lib.capability.fluidhandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class FluidHandler extends FluidTank implements INBTSerializable<NBTTagCompound> {

	private final List<IFluidHandlerChangedListener> listeners;
	private Predicate<FluidStack> canFillFluidType;
	private Predicate<FluidStack> canDrainFluidType;

	public FluidHandler(int capacity) {
		super(capacity);
		this.listeners = new ArrayList<>();
	}

	public FluidHandler(@Nullable FluidStack fluidStack, int capacity) {
		super(fluidStack, capacity);
		this.listeners = new ArrayList<>();
	}

	public FluidHandler(Fluid fluid, int amount, int capacity) {
		super(fluid, amount, capacity);
		this.listeners = new ArrayList<>();
	}

	public boolean canFill() {
		return canFill;
	}

	public boolean canDrain() {
		return canDrain;
	}

	public void setCanFill(boolean canFill) {
		super.setCanFill(canFill);
		this.canFillFluidType = null;
	}

	public void setCanDrain(boolean canDrain) {
		super.setCanDrain(canDrain);
		this.canDrainFluidType = null;
	}

	public FluidHandler setCanFill(Predicate<FluidStack> condition) {
		this.canFill = true;
		this.canFillFluidType = condition;
		return this;
	}

	public FluidHandler setCanDrain(Predicate<FluidStack> condition) {
		this.canDrain = true;
		this.canDrainFluidType = condition;
		return this;
	}

	public FluidHandler setCanFill(FluidStack stack) {
		if (stack == null) {
			this.setCanFill(false);
			return this;
		}
		this.setCanFill(stack::isFluidEqual);
		return this;
	}

	public FluidHandler setCanDrain(FluidStack stack) {
		if (stack == null) {
			this.setCanDrain(false);
			return this;
		}
		this.setCanDrain(stack::isFluidEqual);
		return this;
	}

	@Override
	public boolean canFillFluidType(FluidStack fluid) {
		if (canFillFluidType != null && !canFillFluidType.test(fluid)) return false;
		return canFill();
	}

	@Override
	public boolean canDrainFluidType(FluidStack fluid) {
		if (canDrainFluidType != null && !canDrainFluidType.test(fluid)) return false;
		return canDrain();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}

	public FluidHandler addListener(IFluidHandlerChangedListener listener) {
		if (this.listeners.contains(listener)) return this;
		this.listeners.add(listener);
		return this;
	}

	@Override
	protected void onContentsChanged() {
		this.listeners.forEach(listener -> listener.onFluidHandlerChanged(this));
	}

}
