package skytheory.lib.container;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.google.common.base.Predicates;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class SlotItemHandler extends net.minecraftforge.items.SlotItemHandler {

	private IItemHandler handler;
	private int index;
	private boolean state;
	private Predicate<EntityPlayer> takable;
	private Predicate<ItemStack> filter;

	public SlotItemHandler(IItemHandler handler, int index) {
		this(handler, index, 0, 0);
		this.state = false;
	}

	public SlotItemHandler(IItemHandler handler, int index, int xPosition, int yPosition) {
		super(handler, index, xPosition, yPosition);
		this.handler = handler;
		this.index = index;
		this.state = true;
		this.takable = Predicates.alwaysTrue();
		this.filter = Predicates.alwaysTrue();
		if (this.index >= handler.getSlots()) {
			throw new IndexOutOfBoundsException(String.format("Index out of bounds. handler size: %d", handler.getSlots()));
		}
	}

	/**
	 * スロットの有効化
	 */
	public void enable() {
		this.state = true;
	}

	/**
	 * スロットの無効化
	 */
	public void disable() {
		this.state = false;
	}

	@Override
	public boolean isEnabled() {
		return this.state;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return takable.test(player);
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int amount) {
		if (handler instanceof IItemHandlerModifiable) {
			IItemHandlerModifiable mhandler = (IItemHandlerModifiable) handler;
			ItemStack slotin =  mhandler.getStackInSlot(index).copy();
			ItemStack result = slotin.splitStack(amount);
			// 覚書：ItemStackHandler.onContentsChangedを呼ぶための処理
			if (slotin.isEmpty()) {
				mhandler.setStackInSlot(index, ItemStack.EMPTY);
			} else {
				mhandler.setStackInSlot(index, slotin);
			}
			return result;
		}
		return super.decrStackSize(amount);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return this.handler.isItemValid(this.index, stack) && this.filter.test(stack);
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
