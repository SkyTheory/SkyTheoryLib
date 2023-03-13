package skytheory.lib.menu;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

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

	public boolean canDragTo(Slot pSlot) {
		return !filterSlots.contains(pSlot.index);
	}

}
