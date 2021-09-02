package skytheory.lib.network.tile;

import java.util.Set;

import org.apache.commons.lang3.Validate;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import skytheory.lib.network.PacketHandler;
import skytheory.lib.util.FacingUtils;
import skytheory.lib.util.NetworkUtils;

public class TileSync {

	/**
	 * サーバーにデータ同期を要求する
	 * @param tile
	 * @param cap
	 * @param facings
	 */
	public static void request(TileEntity tile, Capability<?> cap, Set<EnumFacing> facings) {
		NetworkUtils.requireClient(tile);
		TileSyncManager.enqueue(tile, cap, facings);
	}


	/**
	 * 全てのクライアントに対してメッセージを送る
	 * @param tile
	 * @param cap
	 */
	public static void sendToClient(TileEntity tile, Capability<?> cap) {
		sendToClient(tile, cap, FacingUtils.SET_SINGLE_NULL);
	}

	/**
	 * 全てのクライアントに対してメッセージを送る
	 * @param tile
	 * @param cap
	 * @param facings
	 */
	public static void sendToClient(TileEntity tile, Capability<?> cap, Set<EnumFacing> facings) {
		NetworkUtils.requireServer(tile);
		Validate.notNull(facings);
		PacketHandler.CHANNEL.sendToAll(new TileSyncMessage(tile, cap, facings));
	}

	/**
	 * サーバーに対してメッセージを送る
	 * @param tile
	 * @param cap
	 */
	public static void sendToServer(TileEntity tile, Capability<?> cap) {
		sendToServer(tile, cap, FacingUtils.SET_SINGLE_NULL);
	}

	/**
	 * サーバーに対してメッセージを送る
	 * @param tile
	 * @param cap
	 * @param facings
	 */
	public static void sendToServer(TileEntity tile, Capability<?> cap, Set<EnumFacing> facings) {
		NetworkUtils.requireClient(tile);
		Validate.notNull(facings);
		PacketHandler.CHANNEL.sendToServer(new TileSyncMessage(tile, cap, facings));
	}
}
