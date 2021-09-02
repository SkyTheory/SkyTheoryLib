package skytheory.lib.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import skytheory.lib.tile.IRotationalTile;
import skytheory.lib.util.EnumRotation;

public abstract class TileRendererFromRotation<T extends TileEntity & IRotationalTile> extends TileRendererBase<T> {

	@Override
	public void preRender(T tile, double x, double y, double z, float partialTicks, float alpha) {
		super.preRender(tile, x, y, z, partialTicks, alpha);
		GlStateManager.translate(0.0d, 1.0d, 0.0d);
		EnumRotation rotation;
		if (tile != null) {
			rotation = tile.getRotation();
		} else {
			rotation = EnumRotation.SOUTH_DOWN_EAST;
		}
		EnumFacing front = rotation.getFront();
		float angle = rotation.getAngle() * 90.0f;
		switch (front) {
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
		GlStateManager.rotate(angle, 0.0f, 0.0f, 1.0f);
		GlStateManager.translate(0.0d, -1.0d, 0.0d);
	}
}
