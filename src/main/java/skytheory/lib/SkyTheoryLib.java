package skytheory.lib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import skytheory.lib.event.SkyTheoryLibItemHandlerEvent;
import skytheory.lib.event.SkyTheoryLibClickEvent;
import skytheory.lib.init.SetupEvent;

@Mod(SkyTheoryLib.MODID)
public class SkyTheoryLib {
	public static final String MODID = "stlib";

	public SkyTheoryLib() {
		
		FMLJavaModLoadingContext.get().getModEventBus().register(SetupEvent.class);

		MinecraftForge.EVENT_BUS.register(SkyTheoryLibItemHandlerEvent.class);
		MinecraftForge.EVENT_BUS.register(SkyTheoryLibClickEvent.class);
	}
	
}
