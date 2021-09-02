package skytheory.lib.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWrenchBlock {

	public void onLeftClickWithWrench(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side);
	public void onRightClickWithWrench(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side);
}
