package skytheory.lib.init.proxy;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.capability.datasync.DataSyncHandler;
import skytheory.lib.config.Config;
import skytheory.lib.init.ResourceRegister;
import skytheory.lib.network.CapsSyncManager;
import skytheory.lib.network.PacketHandler;
import skytheory.lib.network.entity.EntitySyncHandler;
import skytheory.lib.network.entity.EntitySyncMessage;
import skytheory.lib.network.entity.EntitySyncRequest;
import skytheory.lib.network.entity.EntitySyncRespond;
import skytheory.lib.network.tile.TileSyncHandler;
import skytheory.lib.network.tile.TileSyncMessage;
import skytheory.lib.network.tile.TileSyncRequest;
import skytheory.lib.network.tile.TileSyncRespond;
import skytheory.lib.util.WrenchRegistry;

public class CommonProxy {

	public Configuration config;

	public void preInit(FMLPreInitializationEvent event) {
		ResourceRegister.registerCapability(DataSyncHandler.class);
        File directory = event.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), SkyTheoryLib.MOD_NAME + ".cfg"));
        Config.readConfig();
	}

	public void init(FMLInitializationEvent event) {
		CapsSyncManager.registerLookUp(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		CapsSyncManager.registerLookUp(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
		CapsSyncManager.registerLookUp(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
		CapsSyncManager.registerLookUp(DataSyncHandler.SYNC_DATA_CAPABILITY);
		PacketHandler.createChannel(EntitySyncHandler.TOCLIENT, EntitySyncMessage.class, Side.CLIENT);
		PacketHandler.createChannel(EntitySyncHandler.TOSERVER, EntitySyncMessage.class, Side.SERVER);
		PacketHandler.createChannel(EntitySyncHandler.REQUEST, EntitySyncRequest.class, Side.SERVER);
		PacketHandler.createChannel(EntitySyncHandler.RESPOND, EntitySyncRespond.class, Side.CLIENT);
		PacketHandler.createChannel(TileSyncHandler.TOCLIENT, TileSyncMessage.class, Side.CLIENT);
		PacketHandler.createChannel(TileSyncHandler.TOSERVER, TileSyncMessage.class, Side.SERVER);
		PacketHandler.createChannel(TileSyncHandler.REQUEST, TileSyncRequest.class, Side.SERVER);
		PacketHandler.createChannel(TileSyncHandler.RESPOND, TileSyncRespond.class, Side.CLIENT);
		WrenchRegistry.init();
	}

	public void postInit(FMLPostInitializationEvent event) {
		WrenchRegistry.postInit();
        if (config.hasChanged()) {
            config.save();
        }
	}

}
