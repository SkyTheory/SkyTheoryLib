package skytheory.lib.capability.itemhandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.base.Predicates;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import skytheory.lib.util.StreamUtils;

public class ItemHandlerFiltered extends ItemHandler {

	public static final String KEY_FILTER = "Filter";
	public static final String KEY_STATE = "ListState";

	public final List<ItemStack> filter;
	public boolean isWhiteList;
	public final boolean storeState;

	public ItemHandlerFiltered(int size) {
		super(size);
		this.filter = new ArrayList<>();
		this.isWhiteList = false;
		this.storeState = false;
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		if (!super.isItemValid(slot, stack)) return false;
		boolean match = this.filter.stream().anyMatch(stack::isItemEqual);
		if (this.isWhiteList) return match;
		return !match;
	}

	public void addFilter(ItemStack stack) {
		if (stack.isEmpty()) return;
		stack = stack.copy();
		stack.setCount(1);
		if (!this.filter.stream().anyMatch(stack::isItemEqual)) {
			this.filter.add(stack);
		}
	}

	public void removeFilter(ItemStack stack) {
		List<ItemStack> list = this.filter.stream()
				.filter(Predicates.not(stack::isItemEqual))
				.collect(Collectors.toList());
		this.filter.clear();
		this.filter.addAll(list);
	}

	public void resetFilter() {
		this.filter.clear();
	}

	public void setListState(boolean isWhiteList) {
		this.isWhiteList = isWhiteList;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		if (this.storeState) {
			nbt.setTag(KEY_FILTER, serializeFilter());
			nbt.setBoolean(KEY_STATE, storeState);
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		if (this.storeState) {
			this.deserializeFilter(nbt.getTagList(KEY_FILTER, Constants.NBT.TAG_COMPOUND));
			if (nbt.hasKey(KEY_STATE, Constants.NBT.TAG_BYTE)) {
				this.isWhiteList = nbt.getBoolean(KEY_STATE);
			}
		}
	}

	public NBTTagList serializeFilter() {
		NBTTagList nbt = new NBTTagList();
		this.filter.stream()
		.filter(StreamUtils.distinctItems())
		.map(stack -> stack.serializeNBT())
		.forEach(nbt::appendTag);
		return nbt;
	}

	public void deserializeFilter(NBTTagList list) {
		this.filter.clear();
		List<NBTTagCompound> tags = new ArrayList<>();
		list.forEach(tag -> tags.add((NBTTagCompound) tag));
		tags.stream()
		.map(nbt -> new ItemStack(nbt))
		.filter(StreamUtils.distinctItems())
		.forEach(this.filter::add);
	}

}
