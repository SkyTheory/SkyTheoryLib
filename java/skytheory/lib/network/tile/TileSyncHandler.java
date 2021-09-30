package skytheory.lib.network.tile;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import skytheory.lib.network.CapsSyncManager;
import skytheory.lib.util.FacingUtils;

public class TileSyncHandler {

	public static IMessageHandler<TileSyncRequest, IMessage> REQUEST = new TileSyncRequestHandler();
	public static IMessageHandler<TileSyncRespond, IMessage> RESPOND = new TileSyncRespondHandler();
	public static IMessageHandler<TileSyncMessage, IMessage> TOCLIENT = new TileSyncToClientHandler();
	public static IMessageHandler<TileSyncMessage, IMessage> TOSERVER = new TileSyncToServerHandler();

	private static class TileSyncRequestHandler implements IMessageHandler<TileSyncRequest, IMessage> {
		@Override
		public IMessage onMessage(TileSyncRequest message, MessageContext ctx) {
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			World world = ctx.getServerHandler().player.world;
			if (world.isBlockLoaded(pos)) {
				TileEntity tile = world.getTileEntity(pos);
				Capability<?> cap = CapsSyncManager.lookup(message.capId);
				Set<EnumFacing> facings = FacingUtils.fromBitFlags(message.bitflag);
				if (tile != null && world.loadedTileEntityList.contains(tile)) {
					// Server側のEntityのデータを送信する
					return new TileSyncRespond(tile, cap, facings);
				}
			}
			// TileEntityが見つからない場合などはこちら
			TileSyncManager.failed(pos);
			return null;
		}
	}

	private static class TileSyncRespondHandler implements IMessageHandler<TileSyncRespond, IMessage> {
		@Override
		public IMessage onMessage(TileSyncRespond message, MessageContext ctx) {
			// クライアント側でデータを受け取り、BlockPosからTileEntityの実体を取得する
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			World world = Minecraft.getMinecraft().player.world;
			if (world.isBlockLoaded(pos)) {
				TileEntity tile = world.getTileEntity(pos);
				Capability<?> cap = CapsSyncManager.lookup(message.capId);
				if (tile != null && world.loadedTileEntityList.contains(tile)) {
					TileSyncManager.process(tile, cap, message.compound);
					return null;
				}
			}
			// 覚書：リクエスト後、データが帰ってくる前にClientのTileEntityがアンロードされた場合
			TileSyncManager.failed(pos);
			return null;
		}
	}

	private static class TileSyncToClientHandler implements IMessageHandler<TileSyncMessage, IMessage> {
		@Override
		public IMessage onMessage(TileSyncMessage message, MessageContext ctx) {
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			World world = Minecraft.getMinecraft().player.world;
			if (world.isBlockLoaded(pos)) {
				TileEntity tile = world.getTileEntity(pos);
				if (tile != null && tile.hasWorld()) {
					Capability<?> cap = CapsSyncManager.lookup(message.capId);
					CapsSyncManager.sync(tile, cap, message.compound);
					return null;
				}
			}
			TileSyncManager.failed(pos);
			return null;
		}
	}

	private static class TileSyncToServerHandler implements IMessageHandler<TileSyncMessage, IMessage> {
		@Override
		public IMessage onMessage(TileSyncMessage message, MessageContext ctx) {
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			World world = ctx.getServerHandler().player.world;
			if (world.isBlockLoaded(pos)) {
				TileEntity tile = world.getTileEntity(pos);
				if (tile != null && tile.hasWorld()) {
					Capability<?> cap = CapsSyncManager.lookup(message.capId);
					CapsSyncManager.sync(tile, cap, message.compound);
					return null;
				}
			}
			TileSyncManager.failed(pos);
			return null;
		}
	}

}
