package skytheory.lib.capability.itemhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class MultiItemHandler implements IItemHandler {

	public List<IItemHandler> handlers;

	public MultiItemHandler(IItemHandler... handlers) {
		this.handlers = new ArrayList<>(Arrays.asList(handlers));
	}

	@Override
	public int getSlots() {
		AtomicInteger i = new AtomicInteger();
		this.handlers.forEach(handler -> i.getAndAdd(handler.getSlots()));
		return i.get();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		Pair<IItemHandler, Integer> index = indexer(slot);
		return index.getLeft().getStackInSlot(index.getRight());
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		Pair<IItemHandler, Integer> index = indexer(slot);
		return index.getLeft().insertItem(index.getRight(), stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		Pair<IItemHandler, Integer> index = indexer(slot);
		return index.getLeft().extractItem(index.getRight(), amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		Pair<IItemHandler, Integer> index = indexer(slot);
		return index.getLeft().getSlotLimit(index.getRight());
	}

	private Pair<IItemHandler, Integer> indexer(int slot) {
		for (IItemHandler handler : handlers) {
			int size = handler.getSlots();
			if (slot >= size) {
				slot -= size;
			} else {
				return Pair.of(handler, slot);
			}
		}
		throw new IllegalArgumentException("Index out of bounds.");
	}

	public IItemHandler getHandler() {
		return this;
	}

}
