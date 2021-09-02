package skytheory.lib.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWrenchType {

	public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing);
	public String getName();
	public default boolean skipActivateBlock(EntityPlayer player, World world, BlockPos pos, EnumFacing side) {return true;}
}
