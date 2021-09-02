package skytheory.lib.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import skytheory.lib.tile.IRotationalTile;
import skytheory.lib.util.EnumRotation;

public class ItemBlockRotational extends ItemBlock {

	private final boolean placemode;

	public ItemBlockRotational(Block block) {
		super(block);
		this.placemode = false;
	}

	public ItemBlockRotational(Block block, boolean placemode) {
		super(block);
		this.placemode = placemode;
	}

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
    		float hitX, float hitY, float hitZ, IBlockState newState) {
    	boolean result = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    	if (result) {
    		TileEntity tile = world.getTileEntity(pos);
    		if (tile instanceof IRotationalTile) {
    			this.setRotation(stack, player, world, pos, newState, side, (IRotationalTile) tile);
    		}
    	}
    	return result;
    }

	public void setRotation(ItemStack stack, EntityPlayer player, World world, BlockPos pos, IBlockState state,
			EnumFacing side, IRotationalTile tile) {
		if (placemode && player.isSneaking()) {
			tile.setRotation(this.getHorizontal(stack, player, world, pos, state, side, tile));
		} else {
			tile.setRotation(this.getRotation(stack, player, world, pos, state, side, tile));
		}
	}

	public EnumRotation getRotation(ItemStack stack, EntityPlayer player, World world, BlockPos pos, IBlockState state,
			EnumFacing side, IRotationalTile tile) {
		if (side.getAxis().isHorizontal()) {
			return EnumRotation.fromFacing(side);
		} else {
			EnumFacing horiz = player.getHorizontalFacing();
			if (side == EnumFacing.UP) {
				horiz = horiz.getOpposite();
			}
			return EnumRotation.fromFacing(side, horiz);
		}
	}

	public EnumRotation getHorizontal(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
			IBlockState state, EnumFacing side, IRotationalTile tile) {
		if (side.getAxis().isHorizontal()) {
			return EnumRotation.fromFacing(side);
		} else {
			EnumFacing horiz = player.getHorizontalFacing().getOpposite();
			return EnumRotation.fromFacing(horiz);
		}
	}
}
