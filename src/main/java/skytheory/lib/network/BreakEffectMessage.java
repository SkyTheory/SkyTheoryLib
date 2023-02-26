package skytheory.lib.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class BreakEffectMessage {

	public final ResourceLocation dimension;
	public final BlockPos pos;
	public final BlockState state;

	public BreakEffectMessage(Level level, BlockPos pos, BlockState state) {
		this(level.dimension().location(), pos, state);
	}

	private BreakEffectMessage(ResourceLocation dimension, BlockPos pos, BlockState state) {
		this.dimension = dimension;
		this.pos = pos;
		this.state = state;
	}

	public static void encode(BreakEffectMessage message, FriendlyByteBuf buf) {
		buf.writeResourceLocation(message.dimension);
		buf.writeBlockPos(message.pos);
		buf.writeInt(Block.getId(message.state));
	}

	public static BreakEffectMessage decode(FriendlyByteBuf buf) {
		ResourceLocation dimension = buf.readResourceLocation();
		BlockPos pos = buf.readBlockPos();
		BlockState state = Block.stateById(buf.readInt());
		return new BreakEffectMessage(dimension, pos, state);
	}

	public static void process(BreakEffectMessage msg, Supplier<NetworkEvent.Context> sup) {
		NetworkEvent.Context ctx = sup.get();
		ctx.setPacketHandled(true);
		ctx.enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handlePacketClient(msg, ctx));
		});
	}

	/**
	 * サーバーから送られたメッセージをクライアント側で受け取った場合、ここで処理する
	 * @param message
	 * @param ctx
	 */
	private static void handlePacketClient(BreakEffectMessage message, NetworkEvent.Context ctx) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		Level level = player.getLevel();
		if (!level.isLoaded(message.pos)) return;
		if (!level.dimension().location().equals(message.dimension)) return;
		level.addDestroyBlockEffect(message.pos, level.getBlockState(message.pos));
	}

}
