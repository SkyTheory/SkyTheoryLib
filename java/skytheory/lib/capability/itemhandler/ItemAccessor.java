package skytheory.lib.capability.itemhandler;

import java.util.function.BiPredicate;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;

/**
 * IItemHandlerをWrapして搬入出の制御を追加する<br>
 * この面からは搬入専用にしたい、などという時に利用することを想定している
 * @author SkyTheory
 *
 */
public class ItemAccessor implements IItemHandler, INBTSerializable<NBTBase> {

	public final IItemHandler handler;

	private BiPredicate<Integer, ItemStack> canInsert;
	private BiPredicate<Integer, ItemStack> canExtract;

	public static IItemHandler insertOnly(IItemHandler... handler) {
		IItemHandler wrap = new ItemAccessor(handler).setCanExtract(false);
		return wrap;
	}

	public static IItemHandler extractOnly(IItemHandler... handler) {
		IItemHandler wrap = new ItemAccessor(handler).setCanInsert(false);
		return wrap;
	}

	public ItemAccessor(IItemHandler... handler) {
		Validate.notEmpty(handler);
		if (handler.length == 1) {
			this.handler = handler[0];
		} else {
			this.handler = new MultiItemHandlerSerializable(handler);
		}
	}

	@Override
	public int getSlots() {
		return this.handler.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.handler.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (this.canInsert == null || this.canInsert.test(slot, stack)) {
			if (this.isItemValid(slot, stack)) {
				return this.handler.insertItem(slot, stack, simulate);
			}
		}
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (this.canExtract == null || this.canExtract.test(slot, this.getStackInSlot(slot))) {
			return this.handler.extractItem(slot, amount, simulate);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return handler.isItemValid(slot, stack);
	}

	@Override
	public int getSlotLimit(int slot) {
		return handler.getSlotLimit(slot);
	}

	public ItemAccessor setCanInsert(boolean canInsert) {
		if (canInsert) {
			this.canInsert = null;
		} else {
			this.canInsert = ((slot, stack) -> false);
		}
		return this;
	}

	public ItemAccessor setCanInsert(BiPredicate<Integer, ItemStack> canInsert) {
		this.canInsert = canInsert;
		return this;
	}

	public ItemAccessor setCanExtract(boolean canExtract) {
		if (canExtract) {
			this.canExtract = null;
		} else {
			this.canExtract = ((slot, stack) -> false);
		}
		return this;
	}

	public ItemAccessor setCanExtract(BiPredicate<Integer, ItemStack> canExtract) {
		this.canExtract = canExtract;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public NBTBase serializeNBT() {
		if (this.handler instanceof INBTSerializable) {
			return ((INBTSerializable<NBTBase>) handler).serializeNBT();
		}
		return new NBTTagCompound();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserializeNBT(NBTBase nbt) {
		if (this.handler instanceof INBTSerializable) {
			((INBTSerializable<NBTBase>) handler).deserializeNBT(nbt);
		}
	}

}
