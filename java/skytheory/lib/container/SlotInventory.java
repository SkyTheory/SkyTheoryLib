package skytheory.lib.container;

import java.util.function.Predicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class SlotInventory extends Slot {

	private boolean state;
	private Predicate<EntityPlayer> takable;
	private Predicate<ItemStack> filter;

	public SlotInventory(IInventory inventoryIn, int index) {
		this(inventoryIn, index, 0, 0);
		this.state = false;
	}

	public SlotInventory(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		this.state = true;
		this.slotNumber = -1;
		this.takable = (player -> true);
		this.filter = (stack -> inventoryIn.isItemValidForSlot(this.getSlotIndex(), stack));
	}

	/**
	 * Container内でのこのスロットの位置を返す
	 * @return
	 */
	public int getSlotNumber() {
		return this.slotNumber;
	}

	/**
	 * スロットの有効化
	 */
	public void enable() {
		this.state = true;
	}

	public void disable() {
		this.state = false;
	}

	@Override
	public boolean isEnabled() {
		return this.state;
	}

	/**
	 * GUIでスロットからアイテムを取り出せるかを設定する<br>
	 * ホッパーなどで搬入できるかを設定したいならこちらではなくItemHandlerを使うこと
	 */
	public void setTakable(boolean takable) {
		this.setTakable(player -> takable);
	}

	/**
	 * GUIでスロットからアイテムを取り出せるかを設定する<br>
	 * ホッパーなどで搬入できるかを設定したいならこちらではなくItemHandlerを使うこと
	 */
	public void setTakable(Predicate<EntityPlayer> takable) {
		this.takable = takable;
	}

	/**
	 * GUIでスロットからアイテムを取り出せるかを設定する<br>
	 * ホッパーなどで搬出できるかを設定したいならこちらではなくItemHandlerを使うこと
	 */
	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return takable.test(player);
	}

	/**
	 * GUIでスロットに対してアイテムを入れられるかを設定する<br>
	 * ホッパーなどで搬出できるかを設定したいならこちらではなくItemHandlerを使うこと
	 */
	public void setFilter(boolean state) {
		this.filter = (stack -> state);
	}

	/**
	 * GUIでスロットに対してアイテムを入れられるかを設定する<br>
	 * ホッパーなどで搬出できるかを設定したいならこちらではなくItemHandlerを使うこと
	 */
	public void setFilter(Predicate<ItemStack> filter) {
		this.filter = filter;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return this.filter.test(stack);
	}

	/**
	 * スロットが空の時に背景に表示させるアイテムを設定する
	 * @param location
	 */
	public void setTexture(ResourceLocation location) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			this.setBackgroundName(location.toString());
		}
	}
}
