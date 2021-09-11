package skytheory.lib.network.entity;

import java.util.Set;

import org.apache.commons.lang3.Validate;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import skytheory.lib.network.PacketHandler;
import skytheory.lib.util.FacingUtils;
import skytheory.lib.util.NetworkUtils;

public class EntitySync {

	/**
	 * サーバーにデータ同期を要求する
	 * @param entity
	 * @param cap
	 */
	public static void request(Entity entity, Capability<?> cap) {
		request(entity, cap, FacingUtils.SET_SINGLE_NULL);
	}

	/**
	 * サーバーにデータ同期を要求する
	 * @param entity
	 * @param cap
	 * @param facings
	 */
	public static void request(Entity entity, Capability<?> cap, Set<EnumFacing> facings) {
		NetworkUtils.requireClient(entity);
		EntitySyncManager.enqueue(entity, cap, facings);
	}

	/**
	 * Entityを追跡中のクライアントに対してメッセージを送信する
	 * @param entity
	 * @param cap
	 * @param facings
	 */
	public static void sendToClientTracking(Entity entity, Capability<?> cap, Set<EnumFacing> facings) {
		NetworkUtils.requireServer(entity);
		PacketHandler.CHANNEL.sendToAllTracking(new EntitySyncMessage(entity, cap, facings), entity);
	}

	/**
	 * 全てのクライアントに対してメッセージを送る
	 * @param entity
	 * @param cap
	 */
	public static void sendToClient(Entity entity, Capability<?> cap) {
		sendToClient(entity, cap, FacingUtils.SET_SINGLE_NULL);
	}

	/**
	 * 全てのクライアントに対してメッセージを送る
	 * @param entity
	 * @param cap
	 * @param facings
	 */
	public static void sendToClient(Entity entity, Capability<?> cap, Set<EnumFacing> facings) {
		NetworkUtils.requireServer(entity);
		Validate.notNull(facings);
		PacketHandler.CHANNEL.sendToAll(new EntitySyncMessage(entity, cap, facings));
	}

	/**
	 * サーバーに対してメッセージを送る
	 * @param entity
	 * @param cap
	 */
	public static void sendToServer(Entity entity, Capability<?> cap) {
		sendToServer(entity, cap, FacingUtils.SET_SINGLE_NULL);
	}

	/**
	 * サーバーに対してメッセージを送る
	 * @param entity
	 * @param cap
	 * @param facings
	 */
	public static void sendToServer(Entity entity, Capability<?> cap, Set<EnumFacing> facings) {
		NetworkUtils.requireClient(entity);
		Validate.notNull(facings);
		PacketHandler.CHANNEL.sendToServer(new EntitySyncMessage(entity, cap, facings));
	}
}
