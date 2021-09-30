package skytheory.lib.network.entity;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import skytheory.lib.network.CapsSyncManager;
import skytheory.lib.util.FacingUtils;

public class EntitySyncHandler {

	public static IMessageHandler<EntitySyncRequest, IMessage> REQUEST = new EntitySyncRequestHandler();
	public static IMessageHandler<EntitySyncRespond, IMessage> RESPOND = new EntitySyncRespondHandler();
	public static IMessageHandler<EntitySyncMessage, IMessage> TOCLIENT = new EntitySyncToClientHandler();
	public static IMessageHandler<EntitySyncMessage, IMessage> TOSERVER = new EntitySyncToServerHandler();

	private static class EntitySyncRequestHandler implements IMessageHandler<EntitySyncRequest, IMessage> {
		@Override
		public IMessage onMessage(EntitySyncRequest message, MessageContext ctx) {
			World world = ctx.getServerHandler().player.world;
			Entity entity = world.getEntityByID(message.entityId);
			if (entity != null  && entity.isAddedToWorld() && world.loadedEntityList.contains(entity)) {
				Capability<?> cap = CapsSyncManager.lookup(message.capId);
				Set<EnumFacing> facings = FacingUtils.fromBitFlags(message.bitflag);
				return new EntitySyncRespond(entity, cap, facings);
			}
			// EntityがDespawnしていた場合などはこちら
			EntitySyncManager.failed(message.entityId);
			return null;
		}
	}

	private static class EntitySyncRespondHandler implements IMessageHandler<EntitySyncRespond, IMessage> {
		@Override
		public IMessage onMessage(EntitySyncRespond message, MessageContext ctx) {
			// クライアント側でデータを受け取り、IDからEntityの実体を取得する
			// 文字に起こすと下手な洒落みたいな状況
			World world = Minecraft.getMinecraft().player.world;
			Entity entity = world.getEntityByID(message.entityId);
			if (entity != null  && entity.isAddedToWorld() && world.loadedEntityList.contains(entity)) {
				Capability<?> cap = CapsSyncManager.lookup(message.capId);
				EntitySyncManager.process(entity, cap, message.compound);
			} else {
				// 覚書：リクエスト後、データが帰ってくる前にClientのEntityがアンロードされた場合
				EntitySyncManager.failed(message.entityId);
			}
			return null;
		}
	}

	private static class EntitySyncToClientHandler implements IMessageHandler<EntitySyncMessage, IMessage> {
		@Override
		public IMessage onMessage(EntitySyncMessage message, MessageContext ctx) {
			World world = Minecraft.getMinecraft().player.world;
			Entity entity = world.getEntityByID(message.entityId);
			if (entity != null  && entity.isAddedToWorld() && world.loadedEntityList.contains(entity)) {
				Capability<?> cap = CapsSyncManager.lookup(message.capId);
				CapsSyncManager.sync(entity, cap, message.compound);
				return null;
			}
			EntitySyncManager.failed(message.entityId);
			return null;
		}
	}

	private static class EntitySyncToServerHandler implements IMessageHandler<EntitySyncMessage, IMessage> {
		@Override
		public IMessage onMessage(EntitySyncMessage message, MessageContext ctx) {
			World world = ctx.getServerHandler().player.world;
			Entity entity = world.getEntityByID(message.entityId);
			if (entity != null  && entity.isAddedToWorld() && world.loadedEntityList.contains(entity)) {
				Capability<?> cap = CapsSyncManager.lookup(message.capId);
				CapsSyncManager.sync(entity, cap, message.compound);
				return null;
			}
			EntitySyncManager.failed(message.entityId);
			return null;
		}
	}

}
