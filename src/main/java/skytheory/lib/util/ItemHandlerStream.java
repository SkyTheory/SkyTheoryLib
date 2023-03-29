package skytheory.lib.util;

import java.util.stream.Stream;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ItemHandlerStream {

	public static ItemStack getStackInSlot(ItemHandlerSlot slot) {
		return slot.getStackInSlot();
	}
	
	public static Stream<ItemHandlerSlot> create(IItemHandler handler) {
		return Stream.iterate(new ItemHandlerSlot(handler, 0), slot -> new ItemHandlerSlot(handler, slot.getSlot() + 1))
				.limit(handler.getSlots());
	}

	public static class ItemHandlerSlot {
		
		private final IItemHandler parent;
		private final int slot;
		
		public ItemHandlerSlot(IItemHandler parent, int slot) {
			this.parent = parent;
			this.slot = slot;
			if (!(parent instanceof IItemHandlerModifiable)) {
				LogUtils.getLogger().warn("Create stream with Unmodifiable ItemHandler.");
			}
		}
		
		public int getSlot() {
			return slot;
		}
		
		public ItemStack getStackInSlot() {
			return parent.getStackInSlot(slot);
		}
		
		public ItemStack insertItem(ItemStack stack, ItemHandlerMode mode) {
			return parent.insertItem(slot, stack, mode.actual());
		}
		
		public ItemStack extractItem(int amount, ItemHandlerMode mode) {
			return parent.extractItem(slot, amount, mode.actual());
		}
		
		public int getSlotLimit() {
			return parent.getSlotLimit(slot);
		}
		
		public boolean isItemValid(ItemStack stack) {
			return parent.isItemValid(slot, stack);
		}
		
		public void setStackInSlot(ItemStack stack) {
			if (parent instanceof IItemHandlerModifiable modifiable) {
				modifiable.setStackInSlot(slot, stack);
			} else {
				throw new UnsupportedOperationException("Parent handler does not support setItemStack: " + parent.getClass().getCanonicalName());
			}
		}
		
		public boolean isEmpty() {
			return getStackInSlot().isEmpty();
		}
		
		public void clear() {
			setStackInSlot(ItemStack.EMPTY);
		}
		
	}
	
}
