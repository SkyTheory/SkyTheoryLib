package skytheory.lib.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public abstract class ContainerBase extends Container {

	public static final int SLOT_SIZE = 18;

	public final EntityPlayer player;

	public ContainerBase(EntityPlayer player) {
		this.player = player;
	}

	/**
	 * Inventoryの内容を全てContainerに追加する
	 * @param handler
	 * @param xPos
	 * @param yPos
	 * @param rowsize
	 * @return
	 */
	public List<SlotItemHandler> addSlotFromInventory(IItemHandler handler, int xPos, int yPos, int rowsize) {
		List<SlotItemHandler> list = new ArrayList<SlotItemHandler>();
		for(int i = 0; i < handler.getSlots(); i++) {
			SlotItemHandler slot = this.addSlotFromInventory(handler, i, xPos, yPos, rowsize);
			list.add(slot);
		}
		return list;
	}

	/**
	 * Inventoryの内容を全てContainerに追加する
	 * Slotは無効化された状態で座標(0, 0)に登録される
	 * @param handler
	 * @return
	 */
	public List<SlotItemHandler> addSlotFromInventory(IItemHandler handler) {
		List<SlotItemHandler> list = new ArrayList<SlotItemHandler>();
		for (int i = 0; i < handler.getSlots(); i++) {
			SlotItemHandler slot = this.addSlotToContainer(new SlotItemHandler(handler, i));
			list.add(slot);
		}
		return list;
	}

	/**
	 * for文などでこの関数を回し、インベントリからコンテナにまとめてスロットを追加する
	 * rowsizeに一列に入るスロットの数を入れる
	 * @param handler
	 * @param index
	 * @param xPos
	 * @param yPos
	 * @param rowsize
	 * @return
	 */
	public SlotItemHandler addSlotFromInventory(IItemHandler handler, int index, int xPos, int yPos, int rowsize) {
		return addSlotFromInventory(handler, index, 0, xPos, yPos, rowsize);
	}

	/**
	 * for文などでこの関数を回し、インベントリからコンテナにまとめてスロットを追加する
	 * rowsizeに一列に入るスロットの数を入れる
	 * indexoffsetに入れた個数分、スロットの描画位置をずらす
	 * @param handler
	 * @param index
	 * @param indexoffset
	 * @param xPos
	 * @param yPos
	 * @param rowsize
	 * @return
	 */
	public SlotItemHandler addSlotFromInventory(IItemHandler handler, int index, int indexoffset, int xPos, int yPos, int rowsize) {
		int rPos = (index + indexoffset) % rowsize;
		int cPos = (index + indexoffset) / rowsize;
		int x = xPos + rPos * 18;
		int y = yPos + cPos * 18;
		SlotItemHandler slot = new SlotItemHandler(handler, index, x, y);
		return this.addSlotToContainer(slot);
	}

	/**
	 * 仮想スロットをインベントリに追加する
	 * 主にフィルターなどに使うことを想定している
	 * @param handler
	 * @param xPos
	 * @param yPos
	 * @param rowsize
	 * @return
	 */
	public List<SlotItemHandler> addFilterFromInventory(IItemHandler handler, int xPos, int yPos, int rowsize) {
		List<SlotItemHandler> list = new ArrayList<SlotItemHandler>();
		for(int i = 0; i < handler.getSlots(); i++) {
			SlotItemHandler slot = this.addFilterFromInventory(handler, i, 0, xPos, yPos, rowsize);
			list.add(slot);
		}
		return list;
	}

	/**
	 * 仮想スロットをインベントリに追加する
	 * for文などでこの関数を回し、インベントリからコンテナにまとめてスロットを追加する
	 * @param handler
	 * @param index
	 * @param xPos
	 * @param yPos
	 * @param rowsize
	 * @return
	 */
	public SlotItemHandler addFilterFronInventory(IItemHandler handler, int index, int xPos, int yPos, int rowsize) {
		return this.addFilterFromInventory(handler, index, 0, xPos, yPos, rowsize);
	}

	/**
	 * 仮想スロットをインベントリに追加する
	 * for文などでこの関数を回し、インベントリからコンテナにまとめてスロットを追加する
	 * indexoffsetに入れた個数分、スロットの描画位置をずらす
	 * @param handler
	 * @param index
	 * @param indexoffset
	 * @param xPos
	 * @param yPos
	 * @param rowsize
	 * @return
	 */
	public SlotItemHandler addFilterFromInventory(IItemHandler handler, int index, int indexoffset, int xPos, int yPos, int rowsize) {
		int rPos = (index + indexoffset) % rowsize;
		int cPos = (index + indexoffset) / rowsize;
		int x = xPos + rPos * 18;
		int y = yPos + cPos * 18;
		SlotItemHandler slot = new SlotFilter(handler, index, x, y);
		return this.addSlotToContainer(slot);
	}

	/*
	 * 単一のスロットを作成し、コンテナに追加するメソッド
	 */

	/**
	 * SlotItemHandlerをコンテナに追加する
	 * @param handler
	 * @param index
	 * @param xPos
	 * @param yPos
	 * @return
	 */
	public SlotItemHandler addSlotToContainer(IItemHandler handler, int index, int xPos, int yPos) {
		SlotItemHandler slot = new SlotItemHandler(handler, index, xPos, yPos);
		return this.addSlotToContainer(slot);
	}

	/**
	 * SlotFilterをコンテナに追加する
	 * @param handler
	 * @param index
	 * @param xPos
	 * @param yPos
	 * @return
	 */
	public SlotItemHandler addFilterToContainer(IItemHandler handler, int index, int xPos, int yPos) {
		SlotItemHandler slot = new SlotFilter(handler, index, xPos, yPos);
		return this.addSlotToContainer(slot);
	}

	/*
	 * スロットをコンテナに登録するメソッド
	 */

	public SlotItemHandler addSlotToContainer(SlotItemHandler slot) {
		super.addSlotToContainer(slot);
		return slot;
	}

	public List<SlotItemHandler> addSlotToContainer(Collection<? extends SlotItemHandler> collection) {
		List<SlotItemHandler> result = new ArrayList<>();
		collection.forEach(slot -> {
			result.add(this.addSlotToContainer(slot));
		});
		return result;
	}

	/**
	 * 登録されているSlotがSlotItemHandlerであることを保証するため、こちらは使用不可<br>
	 * 引数がSlotItemHandlerである同名メソッドを使用すること
	 */
	@Override
	@Deprecated
	public final Slot addSlotToContainer(Slot slot) {
		throw new UnsupportedOperationException("Use overload method. (args: SlotItemHandler)");
	}

	/**
	 * バニラのコードは簡単に無限ループを起こしかねないのでOverride
	 */
	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		if (slotId >= 0) {
			Slot slot = this.inventorySlots.get(slotId);
			if (slot instanceof SlotFilter) {
				ItemStack itemstack = player.inventory.getItemStack().copy();
				if (itemstack.isEmpty()) {
					slot.putStack(ItemStack.EMPTY);
				} else {
					itemstack.setCount(1);
					if (slot.getHasStack()) {
						if (ItemStack.areItemStacksEqual(slot.getStack(), itemstack)) {
							slot.putStack(ItemStack.EMPTY);
						} else {
							slot.putStack(itemstack);
						}
					} else {
						slot.putStack(itemstack);
					}
				}
				this.detectAndSendChanges();
				return ItemStack.EMPTY;
			}
			if (clickTypeIn == ClickType.QUICK_MOVE) {
				if (!slot.canTakeStack(player)) {
					return ItemStack.EMPTY;
				}
				ItemStack prevStack;
				ItemStack stack;
				do {
					prevStack = slot.getStack().copy();
					stack = this.transferStackInSlot(player, slotId);
				} while (!stack.isEmpty() && !ItemStack.areItemStacksEqual(prevStack, stack));
				this.detectAndSendChanges();
				return stack.copy();
			}
		}
		ItemStack stack = super.slotClick(slotId, dragType, clickTypeIn, player);
		this.detectAndSendChanges();
		return stack;
	}

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		if (SlotCraftResult.class.isInstance(slotIn)) return false;
		if (SlotFilter.class.isInstance(slotIn)) return false;
		if (!slotIn.canTakeStack(player)) return false;
		return super.canMergeSlot(stack, slotIn);
	}

	protected void removeSlot(Slot slot) {
		super.inventorySlots.remove(slot);
	}

	protected void removeAllSlot(Collection<? extends Slot> slots) {
		super.inventorySlots.removeAll(slots);
	}
}
