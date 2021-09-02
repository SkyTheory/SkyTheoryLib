package skytheory.lib.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import skytheory.lib.tile.IRotationalTile;

/**
 * ItemBlockRotationalと同じく、24方向の向きを持つTileEntityを設置するItemBlock<br>
 * ただし、設置時の初期方向が水平4方向に限定されている<br>
 * 回転を制限させたいのなら別途TileEntityの方でもコーディングすること
 * @author SkyTheory
 *
 */
public class ItemBlockHorizontal extends ItemBlockRotational {

	public ItemBlockHorizontal(Block block) {
		super(block);
	}

	@Override
	public void setRotation(ItemStack stack, EntityPlayer player, World world, BlockPos pos, IBlockState state,
			EnumFacing side, IRotationalTile tile) {
		tile.setRotation(this.getHorizontal(stack, player, world, pos, state, side, tile));
	}
}
