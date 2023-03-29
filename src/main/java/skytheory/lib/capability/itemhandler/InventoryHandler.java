package skytheory.lib.capability.itemhandler;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntConsumer;

import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import skytheory.lib.util.ItemHandlerStream.ItemHandlerSlot;

public class InventoryHandler extends AbstractList<ItemHandlerSlot> implements IItemHandlerModifiable, INBTSerializable<CompoundTag> {

	private final ItemStack[] items;
	private final List<IntConsumer> changedListener;
	private final List<Runnable> loadListener;

	public InventoryHandler(int size) {
		this.items = new ItemStack[size];
		this.clear();
		this.changedListener = new ArrayList<>();
		this.loadListener = new ArrayList<>();
	}

	@Override
	public int size() {
		return items.length;
	}

	@Override
	public ItemHandlerSlot get(int index) {
		return new ItemHandlerSlot((IItemHandler)this, index);
	}

	@Override
	public void clear() {
		for (int i = 0; i < size(); i++) {
			items[i] = ItemStack.EMPTY;
		}
	}

	@Override
	public boolean isEmpty() {
		return this.stream().allMatch(ItemHandlerSlot::isEmpty);
	}

	@Override
	public int getSlots() {
		return size();
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		return items[slot];
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) return ItemStack.EMPTY;
		if (!isItemValid(slot, stack)) return stack;
		ItemStack current = items[slot];
		if (!current.isEmpty() && !ItemHandlerHelper.canItemStacksStack(current, stack)) return stack;
		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
		int quantity = Math.min(limit - current.getCount(), stack.getCount());
		if (quantity <= 0) return ItemStack.EMPTY;
		ItemStack remain = ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - quantity);
		if (!simulate) {
			ItemStack contents = ItemHandlerHelper.copyStackWithSize(stack, current.getCount() + quantity);
			items[slot] = contents.isEmpty() ? ItemStack.EMPTY : contents;
			ItemStack moved = ItemHandlerHelper.copyStackWithSize(stack, quantity);
			this.onInserted(slot, moved);
		}
		return remain;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount <= 0) return ItemStack.EMPTY;
		ItemStack current = items[slot];
		if (current.isEmpty()) return ItemStack.EMPTY;
		int quantity = Math.min(current.getCount(), amount);
		ItemStack extracted = ItemHandlerHelper.copyStackWithSize(current, quantity);
		if (!simulate) {
			current.shrink(quantity);
			if (current.isEmpty()) items[slot] = ItemStack.EMPTY;
			this.onExtracted(slot, extracted);
		}
		return extracted;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return true;
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		stack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
		items[slot] = stack;
		onSet(slot, stack.copy());
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		ListTag items = this.stream()
				.map(ItemHandlerSlot::getStackInSlot)
				.map(ItemStack::serializeNBT)
				.collect(ListTag::new, ListTag::add, ListTag::addAll);
		tag.put("Items", items);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		Iterator<ItemStack> itr = nbt.getList("Items", Tag.TAG_COMPOUND).stream()
		.map(CompoundTag.class::cast)
		.map(ItemStack::of)
		.iterator();
		for (int i = 0; i < size(); i++) items[i] = itr.hasNext() ? itr.next() : ItemStack.EMPTY;
		onLoad();
	}

	public void addChangedListener(IntConsumer action) {
		this.changedListener.add(action);
	}
	
	public void addChangedListener(Runnable action) {
		this.changedListener.add(value -> action.run());
	}

	public void addLoadListener(Runnable action) {
		this.loadListener.add(action);
	}
	
	/**
	 * アイテムが搬入された時に呼ばれる
	 * 引数はインデックスとハンドラに搬入されたアイテムのコピー
	 * @param slot
	 * @param A copy of the ItemStack inserted into this. 
	 */
	protected void onInserted(int slot, ItemStack item) {
		this.onChanged(slot);
	}

	/**
	 * アイテムが搬出された時に呼ばれる
	 * 引数はインデックスとハンドラに搬出されたアイテムのコピー
	 * @param slot
	 * @param A copy of the ItemStack extracted from this.
	 */
	protected void onExtracted(int slot, ItemStack item) {
		this.onChanged(slot);
	}

	/**
	 * アイテムが設定された時に呼ばれる
	 * 設定はインデックスとハンドラに設定されたアイテムのコピー
	 * @param slot
	 * @param  A copy of the ItemStack set in the slot.
	 */
	protected void onSet(int slot, ItemStack item) {
		this.onChanged(slot);
	}
	
	/**
	 * 内容に変化があった時に呼ばれる
	 * @param slot
	 */
	protected void onChanged(int slot) {
		this.changedListener.forEach(t -> t.accept(slot));
	}
	
	/**
	 * NBTからデータを読み込んだ時に呼ばれる
	 */
	protected void onLoad() {
		this.loadListener.forEach(t -> t.run());
	}
	
}