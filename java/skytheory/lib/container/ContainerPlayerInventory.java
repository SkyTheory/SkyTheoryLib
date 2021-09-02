package skytheory.lib.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerPlayerInventory extends ContainerPlayerInventoryBase {

	public ContainerPlayerInventory(EntityPlayer player, int offsetX, int offsetY) {
		super(player, offsetX, offsetY);
		this.addPlayerSlots();
	}

	/**
	 * indexに応じて別のメソッドを呼ぶ<br>
	 * ホットバー：transferFromPlayerHotBar<br>
	 * インベントリ：transferFromPlayerInventory<br>
	 * それ以外：transferFromContainer<br>
	 * オーバーライドするならそちらを書き換えることを推奨
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		Slot slot = this.inventorySlots.get(index);
		if (playerHotBar.contains(slot)) {
			this.transferFromPlayerHotBar(player, slot);
			return ItemStack.EMPTY;
		} else if (playerInventory.contains(slot)) {
			this.transferFromPlayerInventory(player, slot);
			return ItemStack.EMPTY;
		}
		transferFromContainer(player, slot);
		return ItemStack.EMPTY;
	}

	public abstract void transferFromPlayerHotBar(EntityPlayer player, Slot slot);
	public abstract void transferFromPlayerInventory(EntityPlayer player, Slot slot);
	public abstract void transferFromContainer(EntityPlayer player, Slot slot);
}
