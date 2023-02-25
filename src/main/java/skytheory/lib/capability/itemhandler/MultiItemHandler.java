package skytheory.lib.capability.itemhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import skytheory.lib.util.ItemHandlerMode;
import skytheory.lib.util.ItemHandlerStream;
import skytheory.lib.util.ItemHandlerStream.ItemHandlerSlot;
public class MultiItemHandler implements IItemHandlerModifiable {

	public final List<IItemHandler> handlers;

	public MultiItemHandler(IItemHandler... handlers) {
		this.handlers = new ArrayList<>(Arrays.asList(handlers));
		for (IItemHandler handler : handlers) {
			if (!(handler instanceof IItemHandlerModifiable)) {
				LogUtils.getLogger().debug("An instance of MultiItemHandler was created from a handler that does not implement IItemHandlerModifiable. Use with caution: " + handler.getClass().getCanonicalName());
			}
		}
	}

	@Override
	public int getSlots() {
		return this.handlers.stream().collect(Collectors.summingInt(handler -> handler.getSlots()));
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return getSlot(slot).getStackInSlot();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return getSlot(slot).insertItem(stack, ItemHandlerMode.of(simulate));
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return getSlot(slot).extractItem(amount, ItemHandlerMode.of(simulate));
	}

	@Override
	public int getSlotLimit(int slot) {
		return getSlot(slot).getSlotLimit();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return getSlot(slot).isItemValid(stack);
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		getSlot(slot).setStackInSlot(stack);
	}
	
	protected ItemHandlerSlot getSlot(int index) {
		List<ItemHandlerSlot> slots = this.handlers.stream().flatMap(ItemHandlerStream::create).toList();
		return slots.get(index);
	}

}