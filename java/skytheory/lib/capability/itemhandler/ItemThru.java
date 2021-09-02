package skytheory.lib.capability.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import skytheory.lib.tile.ISidedTile;
import skytheory.lib.util.EnumSide;

/**
 * 第一引数のTileEntityが受け取ったアイテムを第二引数のSideにあるTileEntityに渡すItemHandler
 * @author SkyTheory
 *
 */
public class ItemThru implements IItemHandler {

	public final TileEntity tile;
	public final ISidedTile sided;
	public final EnumSide side;

	public <T extends TileEntity & ISidedTile> ItemThru(T tile, EnumSide side) {
		this.tile = tile;
		this.sided = tile;
		this.side = side;
	}

	public IItemHandler getItemHandler() {
		EnumFacing direction = sided.getFacing(side);
		TileEntity target = tile.getWorld().getTileEntity(tile.getPos().offset(direction));
		if (target != null ) {
			return target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite());
		}
		return null;
	}

	@Override
	public int getSlots() {
		IItemHandler handler = this.getItemHandler();
		if (handler != null) return handler.getSlots();
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		IItemHandler handler = this.getItemHandler();
		if (handler != null) return handler.getStackInSlot(slot);
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		IItemHandler handler = this.getItemHandler();
		if (handler != null) return handler.insertItem(slot, stack, simulate);
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		IItemHandler handler = this.getItemHandler();
		if (handler != null) return handler.extractItem(slot, amount, simulate);
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		IItemHandler handler = this.getItemHandler();
		if (handler != null) return handler.getSlotLimit(slot);
		return 0;
	}

}
