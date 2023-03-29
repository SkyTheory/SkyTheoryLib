package skytheory.lib.capability.itemhandler;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemAccessorInsertOnly extends ItemHandlerWrapper {

	public ItemAccessorInsertOnly(IItemHandler handler) {
		super(handler);
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}
	
}
