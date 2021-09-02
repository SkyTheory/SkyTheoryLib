package skytheory.lib.tile;

import net.minecraft.util.EnumFacing;
import skytheory.lib.util.EnumSide;

public interface ISidedTile {

	public EnumFacing getFacing(EnumSide side);

	public EnumSide getSide(EnumFacing facing);
}
