package skytheory.lib.network.tile;

import java.util.Objects;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.network.CapsSyncManager;
import skytheory.lib.util.FacingUtils;

public class TileSyncHandler {

	public static IMessageHandler<TileSyncRequest, IMessage> REQUEST = new TileSyncRequestHandler();
	public static IMessageHandler<TileSyncRespond, IMessage> RESPOND = new TileSyncRespondHandler();
	public static IMessageHandler<TileSyncMissing, IMessage> MISSING = new TileSyncMissingHandler();
	public static IMessageHandler<TileSyncMessage, IMessage> TOCLIENT = new TileSyncToClientHandler();
	public static IMessageHandler<TileSyncMessage, IMessage> TOSERVER = new TileSyncToServerHandler();

	private static class TileSyncRequestHandler implements IMessageHandler<TileSyncRequest, IMessage> {
		@Override
		public IMessage onMessage(TileSyncRequest message, MessageContext ctx) {
			int queueId = message.queueId;
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			TileEntity tile = ctx.getServerHandler().player.world.getTileEntity(pos);
			Capability<?> cap = CapsSyncManager.lookup(message.capId);
			Set<EnumFacing> facings = FacingUtils.fromBitFlags(message.bitflag);
			if (Objects.isNull(tile)) {
				// TileEntityが見つからない場合などはこちら
				return new TileSyncMissing(queueId);
			}
			// Server側のEntityのデータを送信する
			return new TileSyncRespond(queueId, tile, cap, facings);
		}
	}

	private static class TileSyncRespondHandler implements IMessageHandler<TileSyncRespond, IMessage> {
		@Override
		public IMessage onMessage(TileSyncRespond message, MessageContext ctx) {
			// クライアント側でデータを受け取り、IDからTileEntityの実体を取得する
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			TileEntity tile = Minecraft.getMinecraft().player.world.getTileEntity(pos);
			if (tile != null) {
				Capability<?> cap = CapsSyncManager.lookup(message.capId);
				TileSyncManager.process(message.queueId, tile, cap, message.compound);
			} else {
				// 覚書：リクエスト後、データが帰ってくる前にClientのTileEntityがアンロードされた場合
				SkyTheoryLib.LOGGER.warn("Sync failed: Tile unloaded.");
				TileSyncManager.failed(message.queueId);
			}
			return null;
		}
	}

	private static class TileSyncMissingHandler implements IMessageHandler<TileSyncMissing, IMessage> {
		@Override
		public IMessage onMessage(TileSyncMissing message, MessageContext ctx) {
			TileSyncManager.failed(message.queueId);
			return null;
		}
	}

	private static class TileSyncToClientHandler implements IMessageHandler<TileSyncMessage, IMessage> {
		@Override
		public IMessage onMessage(TileSyncMessage message, MessageContext ctx) {
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			TileEntity tile = Minecraft.getMinecraft().player.world.getTileEntity(pos);
			if (tile != null) {
				Capability<?> cap = CapsSyncManager.lookup(message.capId);
				CapsSyncManager.sync(tile, cap, message.compound);
			}
			return null;
		}
	}

	private static class TileSyncToServerHandler implements IMessageHandler<TileSyncMessage, IMessage> {
		@Override
		public IMessage onMessage(TileSyncMessage message, MessageContext ctx) {
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			TileEntity tile = ctx.getServerHandler().player.world.getTileEntity(pos);
			if (tile != null) {
				Capability<?> cap = CapsSyncManager.lookup(message.capId);
				CapsSyncManager.sync(tile, cap, message.compound);
			}
			return null;
		}
	}

}
