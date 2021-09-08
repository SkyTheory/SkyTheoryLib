package skytheory.lib.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import skytheory.lib.capability.itemhandler.ItemHandler;
import skytheory.lib.tile.ISidedTile;

public class ItemHandlerUtils {

	/**
	 * Facing = nullに格納されたItemHandlerを取得する
	 * @param capobject
	 * @return itemhandler
	 */
	public static IItemHandler getItemHandler(ICapabilityProvider capobject) {
		return  getItemHandler(capobject, null);
	}

	/**
	 * @param capobject
	 * @param facing
	 * @return itemhandler
	 */
	public static IItemHandler getItemHandler(ICapabilityProvider capobject, EnumFacing facing) {
		return capobject.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
	}

	/**
	 * EnumSideからIItemHandlerを取得する
	 * @param <T>
	 * @param tile
	 * @param side
	 * @return
	 */
	public static <T extends TileEntity & ISidedTile> IItemHandler getItemHandlerFromSide(T tile, EnumSide side) {
		EnumFacing f = tile.getFacing(side);
		return getItemHandler(tile, f);
	}

	/**
	 * IItemHandlerの中身が空かを判定する
	 * @param handler
	 * @return
	 */
	public static boolean isEmpty(IItemHandler... handlers) {
		for (IItemHandler handler : handlers) {
			if (!hasEmptySlots(handler, handler.getSlots())) return false;
		}
		return true;
	}

	/**
	 * IItemHandlerに空きスロットがあるかを判定する
	 * @param handler
	 * @return
	 */
	public static boolean hasEmptySlot(IItemHandler handler) {
		return hasEmptySlots(handler, 1);
	}

	/**
	 * IItemHandlerに空きスロットが必要数分あるかを判定する
	 * @param handler
	 * @return
	 */
	public static boolean hasEmptySlots(IItemHandler handler, int count) {
		int quo = 0;
		for (int i = 0; i < handler.getSlots(); i++) {
			if (handler.getStackInSlot(i).isEmpty()) quo++;
			if (count == quo) return true;
		}
		return false;
	}

	public static boolean hasItemStack(IItemHandler handler, ItemStack stack) {
		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack slotItem = handler.getStackInSlot(i);
			if (slotItem.isItemEqual(stack)) return true;
		}
		return false;
	}

	public static List<ItemStack> getItemStackList(IItemHandler handler) {
		List<ItemStack> result = new ArrayList<>();
		for (int i = 0; i < handler.getSlots(); i++) {
			result.add(handler.getStackInSlot(i).copy());
		}
		return result;
	}

	public static boolean putItemFromPlayer(IItemHandler handler, int index, EntityPlayer player, boolean simulate) {
		ItemStack stack = player.getHeldItemMainhand().copy();
		if (stack.isEmpty()) return false;
		ItemStack handheld = stack.copy();
		ItemStack remainder = handler.insertItem(index, handheld, simulate);
		if (!simulate) {
			player.inventory.setInventorySlotContents(player.inventory.currentItem, remainder);
		}
		return !stack.equals(remainder);
	}

	public static void dropInventoryItems(World world, BlockPos pos, IItemHandler handler) {
		Vec3d position = new Vec3d(pos);
		position.addVector(0.5d, 0.0d, 0.5d);
		dropInventoryItems(world, position, handler);
	}

	public static void dropInventoryItems(World world, Vec3d vec3d, IItemHandler handler) {
		if (handler == null) return;
		for (int i = 0; i < handler.getSlots(); i++) {
			net.minecraft.inventory.InventoryHelper.spawnItemStack(world, vec3d.x, vec3d.y, vec3d.z, handler.getStackInSlot(i));
		}
	}

	public static void transfer(IItemHandler source, IItemHandler destination, int max) {
		for (SlotProperties prop : iterator(source)) {
			ItemStack transfer = source.extractItem(prop.index, max, true).copy();
			if (!transfer.isEmpty()) {
				ItemStack remain = ItemHandlerHelper.insertItem(destination, transfer, true);
				if (remain.getCount() != transfer.getCount()) {
					int size = transfer.getCount() - remain.getCount();
					ItemStack stack = ItemHandlerHelper.copyStackWithSize(transfer, size);
					source.extractItem(prop.index, size, false);
					ItemHandlerHelper.insertItem(destination, stack, false);
					return;
				}
			}
		}
	}

	/**
	 * 加工マシンなどによる参照のために内容のコピーを保持するItemHandlerを作成する
	 * @param IItemHandler
	 * @return ItemHandler with copy of handler contents
	 */
	public static ItemHandler copyOf(IItemHandler handler) {
		ItemHandler result = new ItemHandler(handler.getSlots());
		for (SlotProperties prop : iterator(handler)) {
			result.setStackInSlot(prop.index, prop.getStack().copy());
			result.setSlotLimit(prop.index, prop.getSlotLimit());
		}
		return result;
	}

	public static Iterable<SlotProperties> iterator(IItemHandler handler) {
		return new Iterable<SlotProperties>() {
			@Override
			public Iterator<SlotProperties> iterator() {
				return new Iterator<SlotProperties>() {

					int i = 0;

					@Override
					public boolean hasNext() {
						return i < handler.getSlots();
					}

					@Override
					public SlotProperties next() {
						return new SlotProperties(handler, i++);
					}

				};
			}
		};
	}

	public static class SlotProperties {

		public final IItemHandler handler;
		public final int index;

		public SlotProperties(IItemHandler handler, int index) {
			this.handler = handler;
			this.index = index;
		}

		public ItemStack getStack() {
			return handler.getStackInSlot(index);
		}

		public int getSlotLimit() {
			return handler.getSlotLimit(index);
		}
	}

}
