package skytheory.lib.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import skytheory.lib.SkyTheoryLib;

public class SkyTheoryLibNetwork {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
	  new ResourceLocation(SkyTheoryLib.MODID, "main"),
	  () -> PROTOCOL_VERSION,
	  PROTOCOL_VERSION::equals,
	  PROTOCOL_VERSION::equals
	);

	private static int ID = 0;
	
	public static void setup() {
		INSTANCE.registerMessage(ID++, BlockMessage.class,
				BlockMessage::encode,
				BlockMessage::decode,
				BlockMessage::process);
		
		INSTANCE.registerMessage(ID++, EntityMessage.class,
				EntityMessage::encode,
				EntityMessage::decode,
				EntityMessage::process);
	}
	
}
