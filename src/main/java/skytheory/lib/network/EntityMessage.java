package skytheory.lib.network;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import skytheory.lib.block.DataSync;

public class EntityMessage {


	private static Logger LOGGER = LogUtils.getLogger();

	private final ResourceLocation dimension;
	private final int id;
	private final CompoundTag data;

	public <T extends Entity & DataSync> EntityMessage(T entity) {
		this(entity.getLevel().dimension().location(), entity.getId(), entity.writeSyncTag());
	}
	
	private EntityMessage(ResourceLocation dimension, int id, CompoundTag data) {
		this.dimension = dimension;
		this.id = id;
		this.data = data;
	}

	public static void encode(EntityMessage message, FriendlyByteBuf buf) {
		buf.writeResourceLocation(message.dimension);
		buf.writeInt(message.id);
		buf.writeNbt(message.data);
	}

	public static EntityMessage decode(FriendlyByteBuf buf) {
		ResourceLocation dimension = buf.readResourceLocation();
		int id = buf.readInt();
		CompoundTag data = buf.readNbt();
		return new EntityMessage(dimension, id, data);
	}

	public static void process(EntityMessage msg, Supplier<NetworkEvent.Context> sup) {
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
	private static void handlePacketClient(EntityMessage message, NetworkEvent.Context ctx) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		Level level = player.level;
		if (!level.dimension().location().equals(message.dimension)) return;
		if (level.getEntity(message.id) instanceof DataSync sync) {
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
	private static void handlePacketServer(EntityMessage message, NetworkEvent.Context ctx) {
		ServerPlayer player = ctx.getSender();
		ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, message.dimension);
		Level level = player.server.getLevel(key);
		if (level != null && level.getEntity(message.id) instanceof DataSync sync) {
			CompoundTag replyTag = sync.writeSyncTag();
			SkyTheoryLibNetwork.sendToClient(player, new EntityMessage(message.dimension, message.id, replyTag));
		} else {
			LOGGER.error("Sync failed: Missing packet object.");
		}
	}

}
