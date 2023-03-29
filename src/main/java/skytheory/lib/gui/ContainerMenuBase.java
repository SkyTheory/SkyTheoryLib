package skytheory.lib.gui;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public abstract class ContainerMenuBase extends AbstractContainerMenu {

	private final IntSet filterSlots = new IntArraySet();

	protected ContainerMenuBase(MenuType<?> pMenuType, int pContainerId) {
		super(pMenuType, pContainerId);
	}

	protected Slot addFilterSlot(Slot pSlot) {
		Slot slot = super.addSlot(pSlot);
		this.filterSlots.add(slot.index);
		return slot;
	}

	@Override
	public void clicked(int pSlotId, int pButton, ClickType pClickType, Player pPlayer) {
		if (pSlotId >= 0) {
			Slot slot = this.slots.get(pSlotId);
			if (filterSlots.contains(slot.index)) {
				ItemStack stack = this.getCarried().copy();
				if (stack.isEmpty() || !slot.mayPlace(stack)) {
					slot.set(ItemStack.EMPTY);
				} else {
					stack.setCount(1);
					slot.set(stack);
				}
				slot.setChanged();
				return;
			}
		}
		super.clicked(pSlotId, pButton, pClickType, pPlayer);
	}

	@Override
	protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
		if (pStack.isEmpty()) return false;
		boolean moved = false;
		int length = pEndIndex - pStartIndex;
		for (int i = 0; i < length; i++) {
			int offset = pReverseDirection ? length - i - 1 : i;
			int index = pStartIndex + offset;
			if (filterSlots.contains(index)) continue;
			Slot slot = this.slots.get(index);
			ItemStack slotItem = slot.getItem();
			if (slotItem.isEmpty() || ItemHandlerHelper.canItemStacksStack(pStack, slotItem)) {
				int quantity = Math.min(slot.getMaxStackSize() - slotItem.getCount(), pStack.getCount());
				if (quantity > 0) {
					slot.set(ItemHandlerHelper.copyStackWithSize(pStack, slotItem.getCount() + quantity));
					pStack.shrink(quantity);
					moved = true;
					if (pStack.isEmpty()) break;
				}
			}
		}
		return moved;
	}

	public boolean canDragTo(Slot pSlot) {
		return !filterSlots.contains(pSlot.index);
	}

}
