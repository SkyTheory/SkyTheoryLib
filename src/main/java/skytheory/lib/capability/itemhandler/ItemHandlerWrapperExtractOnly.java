package skytheory.lib.capability.itemhandler;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerWrapperExtractOnly extends ItemHandlerWrapper {

	public ItemHandlerWrapperExtractOnly(IItemHandler handler) {
		super(handler);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		return stack.copy();
	}
}
