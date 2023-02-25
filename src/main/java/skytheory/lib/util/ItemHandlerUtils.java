package skytheory.lib.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import skytheory.lib.util.ItemHandlerStream.ItemHandlerSlot;

public class ItemHandlerUtils {

	/**
	 * IItemHandlerModifiableの中身を空にする
	 * @param handler
	 */
	public static void clear(IItemHandlerModifiable handler) {
		ItemHandlerStream.create(handler).forEach(slot -> slot.clear());
	}

	/**
	 * 中身が空であるかどうかを判定する
	 * @param handler
	 * @return
	 */
	public static boolean isEmpty(IItemHandler handler) {
		return ItemHandlerStream.create(handler).allMatch(slot -> slot.isEmpty());
	}

	/**
	 * アイテムを搬入する
	 * @param handler
	 * @param stack
	 * @param mode
	 * @return remainder
	 */
	public static ItemStack insertItem(IItemHandler handler, ItemStack stack) {
		return insertItem(handler, stack, ItemHandlerMode.EXECUTE);
	}
	
	/**
	 * アイテムを搬入する
	 * @param handler
	 * @param stack
	 * @param mode
	 * @return remainder
	 */
	public static ItemStack insertItem(IItemHandler handler, ItemStack stack, ItemHandlerMode mode) {
		return ItemHandlerHelper.insertItem(handler, stack, mode.actual());
	}

	/**
	 * 複数のアイテムをまとめて搬入する
	 * @param dest
	 * @param collection
	 * @return remainder
	 */
	public static List<ItemStack> insertItems(IItemHandler dest, Collection<ItemStack> collection) {
		return insertItems(dest, collection, ItemHandlerMode.EXECUTE);
	}

	/**
	 * 複数のアイテムをまとめて搬入する
	 * シミュレーションの際、isItemValidやgetSlotLimitが動的なItemHandlerには実際と異なる値を返す場合がある
	 * 対象の型が自明の場合にのみの使用にとどめるのが無難 (e.g. 自作加工機械のアウトプットの検証)
	 * @param dest
	 * @param itemStacks
	 * @param mode
	 * @return remainder
	 * @deprecated Use caution when simulating.
	 */
	@Deprecated
	public static List<ItemStack> insertItems(IItemHandler dest, Collection<ItemStack> itemStacks, ItemHandlerMode mode) {
		List<ItemStack> result = itemStacks.stream()
				.filter(stack -> !stack.isEmpty())
				.map(stack -> stack.copy())
				.toList();
		ItemHandlerStream.create(dest).forEach(slot -> {
			ItemStack exist = slot.getStackInSlot();
			int margin = Math.min(slot.getSlotLimit(), exist.getMaxStackSize()) - exist.getCount();
			Iterator<ItemStack> it = result.iterator();
			while(it.hasNext() && margin > 0) {
				ItemStack stack = it.next();
				ItemStack remain = slot.insertItem(stack, mode);
				int moved = stack.getCount() - remain.getCount();
				if (mode == ItemHandlerMode.SIMULATE) {
					if (exist.isEmpty()) {
						exist = stack;
						margin = Math.min(slot.getSlotLimit(), exist.getMaxStackSize()) - exist.getCount();
					}
					if (ItemHandlerHelper.canItemStacksStack(exist, stack)) {
						moved = Math.min(margin, moved);
						margin -= moved;
					} else {
						moved = 0;
					}
				}
				stack.shrink(moved);
				if (stack.isEmpty()) {
					it.remove();
				}
			}
		});
		return result;
	}

	/**
	 * 先頭から順にいずれかのアイテムを取り出す
	 * @param handler
	 * @param amount
	 * @return extracted
	 */
	public static ItemStack extractItem(IItemHandler handler, int amount) {
		return extractItem(handler, amount, ItemHandlerMode.EXECUTE);
	}

	/**
	 * 先頭から順にいずれかのアイテムを取り出す
	 * @param handler
	 * @param amount
	 * @param mode
	 * @return extracted
	 */
	public static ItemStack extractItem(IItemHandler handler, int amount, ItemHandlerMode mode) {
		Optional<ItemHandlerSlot> slot = ItemHandlerStream.create(handler)
				.filter(s -> !s.extractItem(amount, ItemHandlerMode.SIMULATE).isEmpty())
				.findFirst();
		return slot.map(s -> s.extractItem(amount, mode)).orElse(ItemStack.EMPTY);
	}

	/**
	 * 末尾から順にいずれかのアイテムを取り出す
	 * @param handler
	 * @param amount
	 * @param simulate
	 * @return extracted
	 */
	public static ItemStack extractLast(IItemHandler handler, int amount) {
		return extractLast(handler, amount, ItemHandlerMode.EXECUTE);
	}
	
	/**
	 * 末尾から順にいずれかのアイテムを取り出す
	 * @param handler
	 * @param amount
	 * @param simulate
	 * @return extracted
	 */
	public static ItemStack extractLast(IItemHandler handler, int amount, ItemHandlerMode mode) {
		Optional<ItemHandlerSlot> slot = ItemHandlerStream.create(handler)
				.filter(s -> !s.extractItem(amount, ItemHandlerMode.SIMULATE).isEmpty())
				.reduce((r, s) -> s);
		return slot.map(s -> s.extractItem(amount, mode)).orElse(ItemStack.EMPTY);
	}
	
	public static ItemStack setStackInSlot(IItemHandler handler, int slot, ItemStack stack) {
		if (handler instanceof IItemHandlerModifiable modifiable) {
			ItemStack ret = modifiable.getStackInSlot(slot);
			modifiable.setStackInSlot(slot, stack);
			return ret;
		}
		throw new UnsupportedOperationException("Handler cannot modify.");
	}

	/**
	 * IItemHandlerから別のIItemHandlerに対してアイテムの移動を試みる
	 * 移し替えられたItemStack、または移動できなかった場合にItemStack.EMPTYを返す
	 * @param source
	 * @param dest
	 * @param amount
	 * @param mode
	 * @return movedItem
	 */
	public static ItemStack tryMoveItem(IItemHandler source, IItemHandler dest, int amount, ItemHandlerMode mode) {
		for (int i = 0; i < source.getSlots(); i++) {
			ItemStack stack = source.extractItem(i, amount, ItemHandlerMode.SIMULATE.actual());
			if (!stack.isEmpty()) {
				int movedCount = stack.getCount() - insertItem(dest, stack, ItemHandlerMode.SIMULATE).getCount();
				if (movedCount > 0) {
					stack = source.extractItem(i, movedCount, mode.actual());
					insertItem(dest, stack, mode);
					return stack.copy();
				}
			}
		}
		return ItemStack.EMPTY;
	}
	
	/**
	 * 対象のプレイヤーにアイテムを渡す
	 * インベントリのスロットに1つでもアイテムが入ったなら、他のスロットを見ることなく処理を終了する
	 * @param player
	 * @param stack
	 * @return remainder
	 */
	public static ItemStack giveItemToPlayer(Player player, ItemStack stack) {
		return giveItemToPlayer(player, stack, ItemHandlerMode.EXECUTE);
	}

	/**
	 * 対象のプレイヤーにアイテムを渡す
	 * インベントリのスロットに1つでもアイテムが入ったなら、他のスロットを見ることなく処理を終了する
	 * @param player
	 * @param stack
	 * @param mode
	 * @return remainder
	 */
	public static ItemStack giveItemToPlayer(Player player, ItemStack stack, ItemHandlerMode mode) {
		if (stack.isEmpty()) return ItemStack.EMPTY;
		int count = stack.getCount();
		int selected = player.getInventory().selected;
		IItemHandler inventory = new PlayerMainInvWrapper(player.getInventory());
		ItemStack remainder = inventory.insertItem(selected, stack, mode.actual());
		if (remainder.getCount() != count) {
			return remainder;
		}
		return ItemHandlerStream.create(inventory)
				.filter(slot -> stack.getCount() != slot.insertItem(stack, ItemHandlerMode.SIMULATE).getCount())
				.findFirst()
				.map(slot -> slot.insertItem(stack, mode)).orElse(stack);
	}

	/**
	 * 対象のプレイヤーにアイテムを渡す
	 * @param player
	 * @param stack
	 * @param mode
	 * @return remainder
	 */
	public static ItemStack giveItemToPlayerEachSlot(Player player, ItemStack stack, ItemHandlerMode mode) {
		if (stack.isEmpty()) return ItemStack.EMPTY;
		int selected = player.getInventory().selected;
		IItemHandler inventory = new PlayerMainInvWrapper(player.getInventory());
		ItemStack remainder = inventory.insertItem(selected, stack, mode.actual());
		Iterator<ItemHandlerSlot> it = ItemHandlerStream.create(inventory).iterator();
		while(!remainder.isEmpty() && it.hasNext()) {
			remainder = it.next().insertItem(remainder, mode);
		}
		return remainder;
	}
	
	/**
	 * 対象のプレイヤーにアイテムを渡し、余りをドロップさせる
	 * @param player
	 * @param stack
	 */
	public static void giveItemToPlayerWithDropItem(Player player, ItemStack stack) {
		ItemStack remainder = giveItemToPlayerEachSlot(player, stack, ItemHandlerMode.EXECUTE);
		dropItemPlayerPosition(player, remainder);
	}
	
	public static void dropItemPlayerPosition(Player player, ItemStack stack) {
		if (!stack.isEmpty()) {
			Level level = player.getLevel();
			if (!level.isClientSide()) {
				Vec3 pos = player.position();
				double dx = RandomHelper.rangeSigned(player.getRandom(), 0.1d);
				double dz = RandomHelper.rangeSigned(player.getRandom(), 0.1d);
				double dy = RandomHelper.range(player.getRandom(), 0.1d) + 0.1d;
				level.addFreshEntity(new ItemEntity(level, pos.x, pos.y, pos.z, stack, dx, dy, dz));
			}
		}
	}
	
	/**
	 * プレイヤーの持っているアイテムを取り出す
	 * @param player
	 * @param hand
	 * @param amount
	 * @param mode
	 * @return taken
	 */
	public static ItemStack takeItemFromPlayer(Player player, InteractionHand hand, int amount, ItemHandlerMode mode) {
		int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : Inventory.SLOT_OFFHAND;
		return takeItemFromPlayer(player, slot, amount, mode);
	}

	/**
	 * プレイヤーの持っているアイテムを取り出す
	 * @param player
	 * @param slot
	 * @param amount
	 * @param mode
	 * @return taken
	 */
	public static ItemStack takeItemFromPlayer(Player player, int slot, int amount, ItemHandlerMode mode) {
		IItemHandler handler = player.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
		if (handler != null) {
			return handler.extractItem(slot, amount, mode.actual());
		}
		return ItemStack.EMPTY;
	}
	
}