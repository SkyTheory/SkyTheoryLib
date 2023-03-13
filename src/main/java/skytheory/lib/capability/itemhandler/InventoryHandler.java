package skytheory.lib.capability.itemhandler;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class InventoryHandler extends ItemStackHandler {

	private final List<ItemHandlerListener> listeners = new ArrayList<>();

	public InventoryHandler(int size) {
		super(size);
	}

	public void addListener(ItemHandlerListener listener) {
		if (!listeners.contains(listener)) this.listeners.add(listener);
	}
	public boolean canInsert(int slot, ItemStack stack) {
		return true;
	}

	private boolean canExtract(int slot) {
		return true;
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (!this.canInsert(slot, stack)) return stack.copy();
		return super.insertItem(slot, stack, simulate);
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (!this.canExtract(slot)) return ItemStack.EMPTY;
		return super.extractItem(slot, amount, simulate);
	}
	@Override
	protected void onContentsChanged(int slot) {
		this.listeners.forEach(listener -> listener.onItemHandlerChanged(this, slot));
	}

	public IItemHandler insertOnly() {
		return new ItemHandlerWrapperInsertOnly(this);
	}

	public IItemHandler extractOnly() {
		return new ItemHandlerWrapperExtractOnly(this);
	}

	public List<ItemStack> setSizeWithOldContents(int size) {
		NonNullList<ItemStack> oldContents = this.stacks;
		List<ItemStack> remains = new ArrayList<>();
		this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
		for (int i = 0; i < Math.min(oldContents.size(), size); i++) {
			this.setStackInSlot(i, oldContents.get(i));
		}
		for (int i = size; i < oldContents.size(); i++) {
			remains.add(oldContents.get(i));
		}
		return remains;
	}

	@Override
	public CompoundTag serializeNBT()  {
		ListTag nbtTagList = new ListTag();
		for (int i = 0; i < stacks.size(); i++) {
			ItemStack stack = stacks.get(i);
			if (!stacks.isEmpty()) {
				CompoundTag itemTag = new CompoundTag();
				itemTag.putInt("Slot", i);
				stack.save(itemTag);
				nbtTagList.add(itemTag);
			}
		}
		CompoundTag nbt = new CompoundTag();
		nbt.put("Items", nbtTagList);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
		for (Tag tag : tagList) {
			if (tag instanceof CompoundTag itemTags) {
				int slot = itemTags.getInt("Slot");
				if (slot >= 0 && slot < stacks.size()) {
					stacks.set(slot, ItemStack.of(itemTags));
				}
			}
		}
		onLoad();
	}

}
