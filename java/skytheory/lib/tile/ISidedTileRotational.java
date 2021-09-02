package skytheory.lib.tile;

import net.minecraft.util.EnumFacing;
import skytheory.lib.util.EnumSide;

public interface ISidedTileRotational extends ISidedTile, IRotationalTile {

	@Override
	public default EnumFacing getFacing(EnumSide side) {
		return this.getRotation().getFacing(side);
	}

	@Override
	public default EnumSide getSide(EnumFacing facing) {
		return this.getRotation().getSide(facing);
	}
}
