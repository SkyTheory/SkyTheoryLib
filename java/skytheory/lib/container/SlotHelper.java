package skytheory.lib.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotHelper {

	/**
	 * アイテムが追加できるかどうかを確認する<br>
	 * sourceにあるアイテムをdestinationのスロットに一部でも加えられるならtrue<br>
	 * それ以外、またはsourceが空ならfalseを返す
	 * @param itemStack1
	 * @param itemStack2
	 * @return
	 */
	public static boolean isStackable(Slot source, Slot destination) {
		ItemStack itemStack1 = source.getStack();
		ItemStack itemStack2 = destination.getStack();
		if (!destination.isItemValid(itemStack1)) return false;
		if (itemStack1.isEmpty()) return false;
		if (itemStack2.isEmpty()) return true;
		if (itemStack1.getItem() != itemStack2.getItem()) return false;
		if (itemStack1.getMetadata() != itemStack2.getMetadata()) return false;
		if (!ItemStack.areItemStackTagsEqual(itemStack1, itemStack2)) return false;
		int limit = Math.min(itemStack2.getMaxStackSize(), destination.getSlotStackLimit());
		if (itemStack2.getCount() >= limit) return false;
		return true;
	}

	/**
	 * アイテムを移動したいSlotを指定し、可能であればアイテムを移動する
	 * @param source
	 * @param destination
	 * @return movedstack
	 */
	public static ItemStack moveItem(Slot source, Slot destination) {
		return SlotHelper.moveItem(source, destination, source.getStack().getCount());
	}

	/**
	 * アイテムを移動したいSlotを指定し、可能であればアイテムを移動する
	 * @param source
	 * @param destination
	 * @param amount
	 * @return movedstack
	 */
	public static ItemStack moveItem(Slot source, Slot destination, int amount) {
		ItemStack itemstack1 = source.getStack();
		ItemStack itemstack2 = destination.getStack();
		if (!isStackable(source, destination)) return ItemStack.EMPTY;
		if (itemstack2.isEmpty() && destination.isItemValid(itemstack1)) {
			int limit = Math.min(itemstack1.getMaxStackSize(), destination.getSlotStackLimit());
			int size = Math.min(itemstack1.getCount(), amount);
			int move = Math.min(limit, size);
			ItemStack split = itemstack1.copy();
			split.setCount(move);
			itemstack1.shrink(move);
			if (itemstack1.isEmpty()) source.putStack(ItemStack.EMPTY);
			destination.putStack(split);
			return split;
		} else {
			int limit = Math.min(itemstack1.getMaxStackSize(), destination.getSlotStackLimit());
			int size = Math.min(itemstack1.getCount(), amount);
			int move = Math.min(limit - itemstack2.getCount(), size);
			if (move > 0) {
				itemstack2.grow(move);
				itemstack1.shrink(move);
				if (itemstack1.isEmpty()) source.putStack(ItemStack.EMPTY);
				return itemstack2;
			}
			return ItemStack.EMPTY;
		}
	}

	/**
	 * アイテムを移動したいSlotを指定し、元のスロットに存在するアイテムを全て移動できるなら移動する
	 * @param source
	 * @param destination
	 * @return movedstack
	 */
	public static ItemStack moveItemWholeStack(Slot source, Slot destination) {
		ItemStack itemstack1 = source.getStack();
		ItemStack itemstack2 = destination.getStack();
		if (!isStackable(source, destination)) return ItemStack.EMPTY;
		if (itemstack2.isEmpty()) {
			if (itemstack1.getCount() > destination.getItemStackLimit(itemstack1)) return ItemStack.EMPTY;
			ItemStack result = itemstack1.copy();
			source.putStack(ItemStack.EMPTY);
			destination.putStack(result);
			return result;
		} else {
			int limit = Math.min(itemstack2.getMaxStackSize(), destination.getSlotStackLimit());
			if (limit - itemstack2.getCount() < itemstack1.getCount()) return ItemStack.EMPTY;
			itemstack2.grow(itemstack1.getCount());
			source.putStack(ItemStack.EMPTY);
			return itemstack2;
		}
	}

	/**
	 * ContainerのtransferStackInSlotから呼ぶなら通常はこちらを推奨
	 * @param source
	 * @param inventorySlots
	 */
	public static void transferStack(Slot source, Iterable<? extends Slot> inventorySlots) {
		for (Slot target : inventorySlots) {
			if (moveItem(source, target) != ItemStack.EMPTY && !source.getHasStack()) break;
		}
	}

	@SafeVarargs
	public static void transferStack(Slot source, Iterable<Slot>...destinations) {
		for (Iterable<Slot> list : destinations) {
			transferStack(source, list);
			if (!source.getHasStack()) break;
		}
	}

	/**
	 * Slot.onSlotChangeとSlot.onTakeを呼びつつアイテムを移動する
	 * クラフト成果などを移動する際に呼ぶ
	 * @param playerIn
	 * @param slot
	 * @param slots
	 */
	public static void transferResult(EntityPlayer playerIn, Slot slot, List<? extends Slot> slots) {
		transferResult(playerIn, slot, slots, false);
	}

	/**
	 * Slot.onSlotChangeとSlot.onTakeを呼びつつアイテムを移動する
	 * クラフト成果などを移動する際に呼ぶ
	 * reverseがtrueの場合、インベントリの後ろから完成品を入れていく
	 * @param playerIn
	 * @param slot
	 * @param slots
	 * @param reverse
	 */
	public static void transferResult(EntityPlayer playerIn, Slot slot, List<? extends Slot> slots, boolean reverse) {
		if (slot.getHasStack()) {
			for (int i = 0, limit = slots.size(); i < limit; i++) {
				int index = reverse ? limit - i - 1: i;
				Slot invslot = slots.get(index);
				ItemStack invstack;
				ItemStack crafted;
				do {
					invstack = invslot.getStack();
					crafted = moveItem(slot, invslot);
					if (!crafted.isEmpty()) {
						slot.onSlotChange(invstack, crafted);
						slot.onTake(playerIn, crafted);
					}
				} while(!crafted.isEmpty());
			}
		}
	}
}
