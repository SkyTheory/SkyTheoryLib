package skytheory.lib.capability.itemhandler;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ItemHandlerWrapper implements IItemHandlerModifiable {

	private static final Logger LOGGER = LogUtils.getLogger();
	
	protected final IItemHandler handler;
	
	public ItemHandlerWrapper(IItemHandler handler) {
		this.handler = handler;
		if (!(handler instanceof IItemHandlerModifiable)) {
			LOGGER.debug("An instance of ItemHandlerWrapper was created from a handler that does not implement IItemHandlerModifiable. Use with caution.");
		}
	}
	
	@Override
	public int getSlots() {
		return this.handler.getSlots();
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		return this.handler.getStackInSlot(slot);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		return this.handler.insertItem(slot, stack, simulate);
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		return this.handler.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return this.handler.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return this.handler.isItemValid(slot, stack);
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		if (handler instanceof IItemHandlerModifiable modifiable) {
			modifiable.setStackInSlot(slot, stack);
		} else {
			throw new UnsupportedOperationException("Parent handler does not implement IItemHandlerModifiable: "+ handler.getClass().getCanonicalName());
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ItemHandlerWrapper another) {
			return this.getClass() == another.getClass() && this.handler.equals(another.handler);
		}
		return false;
	}
	

}