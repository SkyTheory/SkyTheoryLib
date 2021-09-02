package skytheory.lib.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import skytheory.lib.util.FacingHelper;

public abstract class TileRendererFromState<T extends TileEntity> extends TileRendererBase<T> {

	@Override
	public void preRender(T tile, double x, double y, double z, float partialTicks, float alpha) {
		super.preRender(tile, x, y, z, partialTicks, alpha);
		GlStateManager.translate(0.0d, 1.0d, 0.0d);
		EnumFacing facing = EnumFacing.SOUTH;
		if (tile != null) {
			EnumFacing f = FacingHelper.getFacingFromState(tile.getWorld().getBlockState(tile.getPos()));
			if (f != null) {
				facing = f;
			}
		}
		switch (facing) {
		case DOWN:
			GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
			break;
		case UP:
			GlStateManager.rotate(270.0f, 1.0F, 0.0F, 0.0F);
			break;
		case NORTH:
			break;
		case SOUTH:
			GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
			break;
		case EAST:
			GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
			break;
		case WEST:
			GlStateManager.rotate(270.0f, 0.0f, 1.0f, 0.0f);
			break;
		default:
			break;
		}
		GlStateManager.translate(0.0d, -1.0d, 0.0d);
	}

}
