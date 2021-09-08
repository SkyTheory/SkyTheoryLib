package skytheory.lib.capability.fluidhandler;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidHandlerInfinite implements IFluidTank, IFluidHandler, INBTSerializable<NBTTagCompound> {

	public static final String KEY_EMPTY = "Empty";

	public FluidStack fluid;
	public final IFluidTankProperties[] prop;

	public FluidHandlerInfinite() {
		this((FluidStack) null);
	}

	public FluidHandlerInfinite(@Nullable Fluid fluid) {
		this(new FluidStack(fluid, Integer.MAX_VALUE));
	}

	public FluidHandlerInfinite(@Nullable FluidStack stack) {
		this.fluid = stack;
		this.prop = new IFluidTankProperties[] {new FluidInfiniteProperty(stack)};
	}

	@Override
	public FluidStack getFluid() {
		return fluid;
	}

	@Override
	public int getFluidAmount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getCapacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return prop;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (resource == null) return 0;
		if (fluid != null && !fluid.isFluidEqual(resource)) return 0;
		return resource.amount;
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (fluid == null || !fluid.isFluidEqual(resource)) return null;
		return resource.copy();
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		FluidStack result = fluid.copy();
		result.amount = maxDrain;
		return result;
	}

	public void setFluid(Fluid fluid) {
		this.fluid = new FluidStack(fluid, Integer.MAX_VALUE);
	}

	public void setFluid(FluidStack fluid) {
		this.fluid = fluid;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		if (this.fluid != null) {
			fluid.writeToNBT(nbt);
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.fluid = FluidStack.loadFluidStackFromNBT(nbt);
	}

	public static class FluidInfiniteProperty implements IFluidTankProperties {

		FluidStack fluid;

		public FluidInfiniteProperty(FluidStack stack) {
			this.fluid = stack;
		}

		@Override
		public FluidStack getContents() {
			return fluid;
		}

		@Override
		public int getCapacity() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean canFill() {
			return true;
		}

		@Override
		public boolean canDrain() {
			return true;
		}

		@Override
		public boolean canFillFluidType(FluidStack fluidStack) {
			if (fluidStack == null) return false;
			if (fluid == null) return true;
			return fluid.isFluidEqual(fluidStack);
		}

		@Override
		public boolean canDrainFluidType(FluidStack fluidStack) {
			return fluid != null && fluid.isFluidEqual(fluidStack);
		}

	}
}
