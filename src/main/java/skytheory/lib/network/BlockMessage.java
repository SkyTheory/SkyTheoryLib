package skytheory.lib.network;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import skytheory.lib.block.DataSync;

public class BlockMessage {

	private static Logger LOGGER = LogUtils.getLogger();

	private final ResourceLocation dimension;
	private final BlockPos pos;
	private final CompoundTag data;

	public <T extends BlockEntity & DataSync> BlockMessage(T blockEntity) {
		this(blockEntity.getLevel().dimension().location(), blockEntity.getBlockPos(), blockEntity.writeSyncTag());
	}
	
	private BlockMessage(ResourceLocation dimension, BlockPos pos, CompoundTag data) {
		this.dimension = dimension;
		this.pos = pos;
		this.data = data;
	}

	public static void encode(BlockMessage message, FriendlyByteBuf buf) {
		buf.writeResourceLocation(message.dimension);
		buf.writeBlockPos(message.pos);
		buf.writeNbt(message.data);
	}

	public static BlockMessage decode(FriendlyByteBuf buf) {
		ResourceLocation dimension = buf.readResourceLocation();
		BlockPos pos = buf.readBlockPos();
		CompoundTag data = buf.readNbt();
		return new BlockMessage(dimension, pos, data);
	}

	public static void process(BlockMessage msg, Supplier<NetworkEvent.Context> sup) {
		NetworkEvent.Context ctx = sup.get();
		ctx.setPacketHandled(true);
		ctx.enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handlePacketClient(msg, ctx));
			DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> handlePacketServer(msg, ctx));
		});
	}

	/**
	 * サーバーから送られたメッセージをクライアント側で受け取った場合、ここで処理する
	 * @param message
	 * @param ctx
	 */
	private static void handlePacketClient(BlockMessage message, NetworkEvent.Context ctx) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		BlockPos pos = message.pos;
		Level level = player.level;
		if (!level.dimension().location().equals(message.dimension)) return;
		if (level.isLoaded(pos) && level.getBlockEntity(pos) instanceof DataSync sync) {
			sync.readSyncTag(message.data);
		} else {
			LOGGER.error("Sync failed: Missing packet object.");
		}
	}

	/**
	 * クライアントから送られたメッセージをサーバー側で受け取った場合、ここで処理する
	 * @param message
	 * @param ctx
	 */
	private static void handlePacketServer(BlockMessage message, NetworkEvent.Context ctx) {
		ServerPlayer player = ctx.getSender();
		ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, message.dimension);
		BlockPos pos = message.pos;
		Level level = player.server.getLevel(key);
		if (level != null && level.isLoaded(pos) && level.getBlockEntity(pos) instanceof DataSync sync) {
			CompoundTag replyTag = sync.writeSyncTag();
			SkyTheoryLibNetwork.sendToClient(player, new BlockMessage(message.dimension, message.pos, replyTag));
		} else {
			LOGGER.error("Sync failed: Missing packet object.");
		}
	}

}
