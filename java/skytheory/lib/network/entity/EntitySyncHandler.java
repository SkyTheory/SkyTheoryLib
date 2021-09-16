package skytheory.lib.network.entity;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.network.CapsSyncManager;
import skytheory.lib.util.FacingUtils;

public class EntitySyncHandler {

	public static IMessageHandler<EntitySyncRequest, IMessage> REQUEST = new EntitySyncRequestHandler();
	public static IMessageHandler<EntitySyncRespond, IMessage> RESPOND = new EntitySyncRespondHandler();
	public static IMessageHandler<EntitySyncMissing, IMessage> MISSING = new EntitySyncMissingHandler();
	public static IMessageHandler<EntitySyncMessage, IMessage> TOCLIENT = new EntitySyncToClientHandler();
	public static IMessageHandler<EntitySyncMessage, IMessage> TOSERVER = new EntitySyncToServerHandler();

	private static class EntitySyncRequestHandler implements IMessageHandler<EntitySyncRequest, IMessage> {
		@Override
		public IMessage onMessage(EntitySyncRequest message, MessageContext ctx) {
			int queueId = message.queueId;
			Entity entity = ctx.getServerHandler().player.world.getEntityByID(message.entityId);
			Capability<?> cap = CapsSyncManager.lookup(message.capId);
			Set<EnumFacing> facings = FacingUtils.fromBitFlags(message.bitflag);
			if (entity != null && entity.isAddedToWorld()) {
				// Server側のEntityのデータを送信する
				return new EntitySyncRespond(queueId, entity, cap, facings);
			} else {
				// EntityがDespawnしていた場合などはこちら
				return new EntitySyncMissing(queueId);
			}
		}
	}

	private static class EntitySyncRespondHandler implements IMessageHandler<EntitySyncRespond, IMessage> {
		@Override
		public IMessage onMessage(EntitySyncRespond message, MessageContext ctx) {
			// クライアント側でデータを受け取り、IDからEntityの実体を取得する
			// 文字に起こすと下手な洒落みたいな状況
			Entity entity = Minecraft.getMinecraft().player.world.getEntityByID(message.entityId);
			if (entity != null && entity.isAddedToWorld()) {
				Capability<?> cap = CapsSyncManager.lookup(message.capId);
				EntitySyncManager.process(message.queueId, entity, cap, message.compound);
			} else {
				// 覚書：リクエスト後、データが帰ってくる前にClientのEntityがアンロードされた場合
				SkyTheoryLib.LOGGER.warn("Sync failed: Entity unloaded.");
				EntitySyncManager.failed(message.queueId);
			}
			return null;
		}
	}

	private static class EntitySyncMissingHandler implements IMessageHandler<EntitySyncMissing, IMessage> {
		@Override
		public IMessage onMessage(EntitySyncMissing message, MessageContext ctx) {
			EntitySyncManager.failed(message.queueId);
			return null;
		}
	}

	private static class EntitySyncToClientHandler implements IMessageHandler<EntitySyncMessage, IMessage> {
		@Override
		public IMessage onMessage(EntitySyncMessage message, MessageContext ctx) {
			Entity entity = Minecraft.getMinecraft().player.world.getEntityByID(message.entityId);
			if (entity != null && entity.isAddedToWorld()) {
				Capability<?> cap = CapsSyncManager.lookup(message.capId);
				CapsSyncManager.sync(entity, cap, message.compound);
			}
			return null;
		}
	}

	private static class EntitySyncToServerHandler implements IMessageHandler<EntitySyncMessage, IMessage> {
		@Override
		public IMessage onMessage(EntitySyncMessage message, MessageContext ctx) {
			Entity entity = ctx.getServerHandler().player.world.getEntityByID(message.entityId);
			if (entity != null && entity.isAddedToWorld()) {
				Capability<?> cap = CapsSyncManager.lookup(message.capId);
				CapsSyncManager.sync(entity, cap, message.compound);
			}
			return null;
		}
	}

}
