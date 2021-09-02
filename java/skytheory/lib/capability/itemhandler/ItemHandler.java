package skytheory.lib.capability.itemhandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandler extends ItemStackHandler {

	private final List<IItemHandlerChangedListener> listeners;
	private final int actuallySize;
	private final int[] limit;
	private BiPredicate<Integer, ItemStack> canInsert;
	private BiPredicate<Integer, ItemStack> canExtract;

	public ItemHandler(int size) {
		super(size);
		this.actuallySize = size;
		this.limit = new int[size];
		this.listeners = new ArrayList<>();
		for (int i = 0; i < size; i++) this.limit[i] = 64;
	}

	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (this.isItemValid(slot, stack)) {
			return insertInternal(slot, stack, simulate);
		}
		return stack;
	}

	public ItemStack insertInternal(int slot, @Nonnull ItemStack stack, boolean simulate) {
		return super.insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (this.canExtract == null || this.canExtract.test(slot, this.getStackInSlot(slot))) {
			return extractInternal(slot, amount, simulate);
		}
		return ItemStack.EMPTY;
	}

	public ItemStack extractInternal(int slot, int amount, boolean simulate) {
		return super.extractItem(slot, amount, simulate);
	}

	/*
	 * 覚書：ItemStackHandlerからの実装ではTagからの読み込みによりインベントリの容量を可変にしていたが
	 * Modのバージョン更新などで容量を変更した際、タグに残った古い容量のデータが使用されてしまうため
	 * コンストラクタで指定した容量にするように変更
	 */
	@Override
	@Deprecated
	public void setSize(int size) {
		super.setSize(actuallySize);
	}

	@Override
	public int getSlotLimit(int slot) {
		return this.limit[slot];
	}

	@Override
	protected void onContentsChanged(int slot) {
		this.listeners.forEach(listener -> listener.onItemHandlerChanged(this, slot));
	}

	public ItemHandler addListener(IItemHandlerChangedListener listener) {
		if (this.listeners.contains(listener)) return this;
		this.listeners.add(listener);
		return this;
	}

	public ItemHandler setSlotLimit(int limit) {
		for (int i = 0; i < this.limit.length; i++) this.setSlotLimit(i, limit);
		return this;
	}

	public ItemHandler setSlotLimit(int slot, int limit) {
		this.limit[slot] = limit;
		return this;
	}

	public ItemHandler setCanInsert(boolean canInsert) {
		if (canInsert) {
			this.canInsert = null;
		} else {
			this.canInsert = ((slot, stack) -> false);
		}
		return this;
	}

	public ItemHandler setCanInsert(BiPredicate<Integer, ItemStack> canInsert) {
		this.canInsert = canInsert;
		return this;
	}

	public ItemHandler setCanExtract(boolean canExtract) {
		if (canExtract) {
			this.canExtract = null;
		} else {
			this.canExtract = ((slot, stack) -> false);
		}
		return this;
	}

	public ItemHandler setCanExtract(BiPredicate<Integer, ItemStack> canExtract) {
		this.canExtract = canExtract;
		return this;
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return this.canInsert == null || this.canInsert.test(slot, stack);
	}

}
