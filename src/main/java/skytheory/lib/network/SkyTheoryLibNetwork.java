package skytheory.lib.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import skytheory.lib.SkyTheoryLib;

public class SkyTheoryLibNetwork {
	private static final String PROTOCOL_VERSION = "1";
	private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
	  new ResourceLocation(SkyTheoryLib.MODID, "main"),
	  () -> PROTOCOL_VERSION,
	  PROTOCOL_VERSION::equals,
	  PROTOCOL_VERSION::equals
	);

	private static int ID = 0;
	
	public static void setup() {
		registerMessage(
				BlockMessage.class,
				BlockMessage::encode,
				BlockMessage::decode,
				BlockMessage::process);

		registerMessage(
				EntityMessage.class,
				EntityMessage::encode,
				EntityMessage::decode,
				EntityMessage::process);

		registerMessage(
				BreakEffectMessage.class,
				BreakEffectMessage::encode,
				BreakEffectMessage::decode,
				BreakEffectMessage::process);
	}
	
	public static <MSG> void registerMessage(Class<MSG> message, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
		INSTANCE.registerMessage(getNextID(), message, encoder, decoder, messageConsumer);
	}
	
	public static <MSG> void sendToClient(Player player, MSG message) {
		if (player instanceof ServerPlayer serverPlayer) {
			INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message);
		} else {
			throw new IllegalArgumentException("The Player instance passed must be a ServerPlayer.");
		}
	}
	
	public static <MSG> void sendToClient(Level level, BlockPos pos, MSG message) {
		INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), message);
	}
	
	public static <MSG> void sendToClient(Entity entity, MSG message) {
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
	}
	
	public static <MSG> void sendToServer(MSG message) {
		INSTANCE.sendToServer(message);
	}
	
	public static int getNextID() {
		return ID++;
	}
	
}
