package skytheory.lib.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import skytheory.lib.SkyTheoryLib;

public class PacketHandler {

	public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(SkyTheoryLib.MOD_ID);

	private static int id;

	/**
	 * 覚書：引数sideにはメッセージを受け取る側のSideを渡すこと
	 */
	public static <S extends IMessage, R extends IMessage> void createChannel(IMessageHandler<S, R> sender, Class<S> msgtype, Side side) {
		CHANNEL.registerMessage(sender, msgtype, id++, side);
	}
}
