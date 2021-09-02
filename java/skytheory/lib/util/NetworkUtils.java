package skytheory.lib.util;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 今のところユーティリティというよりバリデーターになってるけれど気にしない
 * @author SkyTheory
 *
 */
public class NetworkUtils {

	/**
	 * 物理クライアントか否かではなく、内部クライアントか否かを返す点には留意すること<br>
	 * @param entity
	 * @return
	 */
	public static boolean isClient(Entity entity) {
		return entity.world.isRemote;
	}

	/**
	 * 物理サーバーか否かではなく、内部サーバーか否かを返す点には留意すること<br>
	 * @param entity
	 * @return
	 */
	public static boolean isServer(Entity entity) {
		return !entity.world.isRemote;
	}

	/**
	 * 物理クライアントか否かではなく、内部クライアントか否かを返す点には留意すること<br>
	 * @param tile
	 * @return
	 */
	public static boolean isClient(TileEntity tile) {
		return tile.getWorld().isRemote;
	}

	/**
	 * 物理サーバーか否かではなく、内部サーバーか否かを返す点には留意すること<br>
	 * @param tile
	 * @return
	 */
	public static boolean isServer(TileEntity tile) {
		return !tile.getWorld().isRemote;
	}

	public static void requireClient(Entity entity) {
		Side side = isClient(entity) ? Side.CLIENT : Side.SERVER;
		ValidateSide(Side.CLIENT, side, entity);
	}

	public static void requireServer(Entity entity) {
		Side side = isClient(entity) ? Side.CLIENT : Side.SERVER;
		ValidateSide(Side.SERVER, side, entity);
	}

	public static void requireClient(TileEntity tile) {
		Side side = isClient(tile) ? Side.CLIENT : Side.SERVER;
		ValidateSide(Side.CLIENT, side, tile);
	}

	public static void requireServer(TileEntity tile) {
		Side side = isClient(tile) ? Side.CLIENT : Side.SERVER;
		ValidateSide(Side.SERVER, side, tile);
	}

	public static void ValidateSide(Side require, Side side, Object obj) {
		Validate(require, side, "Invalid side. requires "+ require.toString() + " side: " + obj.getClass());
	}

	public static void Validate(Side require, Side side, String detail) {
		if (require != side) {
			throw new IllegalStateException(detail);
		}
	}

}
