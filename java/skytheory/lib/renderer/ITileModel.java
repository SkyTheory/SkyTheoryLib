package skytheory.lib.renderer;

import net.minecraft.tileentity.TileEntity;

/**
 * 描画したいTileEntityのモデルに実装すること
 * @author SkyTheory
 *
 */
public interface ITileModel<T extends TileEntity> {

	public void render(T tile, float partialtick);
}
