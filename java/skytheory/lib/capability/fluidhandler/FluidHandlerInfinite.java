package skytheory.lib.capability.fluidhandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidHandlerInfinite implements IFluidTank, IFluidHandler, INBTSerializable<NBTTagCompound> {

	private final IFluidTankProperties[] prop;
	private final List<IFluidHandlerChangedListener> listeners;
	private FluidStack fluid;
	private TileEntity tile;
	private Predicate<FluidStack> canFill;
	private Predicate<FluidStack> canDrain;

	public FluidHandlerInfinite() {
		this((FluidStack) null);
	}

	public FluidHandlerInfinite(@Nullable Fluid fluid) {
		this(new FluidStack(fluid, Integer.MAX_VALUE));
	}

	public FluidHandlerInfinite(@Nullable FluidStack stack) {
		this.fluid = stack;
		this.prop = new IFluidTankProperties[] {new FluidInfiniteProperty(this)};
		this.listeners = new ArrayList<>();
	}

	public FluidHandlerInfinite setCanFill(Predicate<FluidStack> canFill) {
		this.canFill = canFill;
		return this;
	}

	public FluidHandlerInfinite setCanDrain(Predicate<FluidStack> canDrain) {
		this.canDrain = canDrain;
		return this;
	}

	public FluidHandlerInfinite setTileEntity(TileEntity tile) {
		this.tile = tile;
		return this;
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
		if (resource.amount == 0) return 0;
		if (canFill != null && !canFill.test(resource)) return 0;
		if (fluid != null && !fluid.isFluidEqual(resource)) return 0;
		int result = resource.amount;
		if (!doFill) return result;
		this.onContentsChanged();
		if (tile != null) {
			FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluid, tile.getWorld(), tile.getPos(), this, result));
		}
		return resource.amount;
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (resource == null) return null;
		if (resource.amount == 0) return null;
		if (fluid == null || !fluid.isFluidEqual(resource)) return null;
		if (canDrain != null && !canDrain.test(resource)) return null;
		FluidStack result = resource.copy();
		if (!doDrain) return result;
		this.onContentsChanged();
		if (tile != null) {
			FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(fluid, tile.getWorld(), tile.getPos(), this, result.amount));
		}
		return result;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		FluidStack toDrain = fluid.copy();
		toDrain.amount = maxDrain;
		return drain(toDrain, doDrain);
	}

	public void setFluid(Fluid fluid) {
		this.fluid = new FluidStack(fluid, Integer.MAX_VALUE);
	}

	public void setFluid(FluidStack fluid) {
		this.fluid = fluid;
	}

	public FluidHandlerInfinite addListener(IFluidHandlerChangedListener listener) {
		if (this.listeners.contains(listener)) return this;
		this.listeners.add(listener);
		return this;
	}

	public void onContentsChanged() {
		this.listeners.forEach(l -> l.onFluidHandlerChanged(this));
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

	protected static class FluidInfiniteProperty implements IFluidTankProperties {

		FluidHandlerInfinite handler;

		public FluidInfiniteProperty(FluidHandlerInfinite handler) {
			this.handler = handler;
		}

		@Override
		public FluidStack getContents() {
			return handler.getFluid();
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
			if (handler.getFluid() == null) return true;
			if (handler.canFill != null && !handler.canFill.test(fluidStack)) return false;
			return handler.getFluid().isFluidEqual(fluidStack);
		}

		@Override
		public boolean canDrainFluidType(FluidStack fluidStack) {
			if (handler.canDrain != null && !handler.canDrain.test(fluidStack)) return false;
			return handler.getFluid() != null && handler.getFluid().isFluidEqual(fluidStack);
		}

	}

}
