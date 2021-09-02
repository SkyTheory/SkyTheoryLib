package skytheory.lib.tile;

import net.minecraft.util.EnumFacing;
import skytheory.lib.util.EnumRotation;
import skytheory.lib.util.EnumSide;

public interface ISidedTileDirectional extends ISidedTile {

	// world.getBlockState(pos).getValue(BlockDirectional.FACING);
	// などが主な方法
	// FacingHelper.getFacingFromState(world.getBlockState(pos));
	// などでも可
	public EnumFacing getFacing();

	@Override
	public default EnumFacing getFacing(EnumSide side) {
		return EnumRotation.fromFacing(getFacing()).getFacing(side);
	}

	@Override
	public default EnumSide getSide(EnumFacing facing) {
		return EnumRotation.fromFacing(getFacing()).getSide(facing);
	}
}
