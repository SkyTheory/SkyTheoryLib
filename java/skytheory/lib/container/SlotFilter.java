package skytheory.lib.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class SlotFilter extends SlotItemHandler {

	public SlotFilter(IItemHandler handler, int index) {
		super(handler, index);
		this.setTakable(false);
	}

	public SlotFilter(IItemHandler handler, int index, int xPosition, int yPosition) {
		super(handler, index, xPosition, yPosition);
		this.setTakable(false);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}
}
