package skytheory.lib.event;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.capability.DataProvider;
import skytheory.lib.capability.datasync.DataSyncHandler;
import skytheory.lib.capability.datasync.IDataSync;
import skytheory.lib.item.IItemInventory;
import skytheory.lib.item.IItemTank;
import skytheory.lib.tile.ITileInventory;
import skytheory.lib.tile.ITileTank;

public class CapabilityEvent {

	public static final ResourceLocation KEY_INVENTORY = new ResourceLocation(SkyTheoryLib.MOD_ID, "Inventory");
	public static final ResourceLocation KEY_FLUID = new ResourceLocation(SkyTheoryLib.MOD_ID, "Fluid");
	public static final ResourceLocation KEY_SYNC = new ResourceLocation(SkyTheoryLib.MOD_ID, "SyncData");

	@SubscribeEvent
	public static void onAttachCapabilityTileEvent(AttachCapabilitiesEvent<TileEntity> event) {
		TileEntity tile = event.getObject();
		if (tile instanceof ITileInventory) {
			ICapabilityProvider provider = ((ITileInventory) tile).createInventoryProvider();
			event.addCapability(KEY_INVENTORY, provider);
		}
		if (tile instanceof ITileTank) {
			ICapabilityProvider provider = ((ITileTank) tile).createFluidProvider();
			event.addCapability(KEY_FLUID, provider);
		}
		if (tile instanceof IDataSync) {
			IDataSync sync = ((IDataSync) tile);
			DataSyncHandler data = new DataSyncHandler(sync);
			ICapabilityProvider provider = new DataProvider<DataSyncHandler>(DataSyncHandler.SYNC_DATA_CAPABILITY, data);
			event.addCapability(KEY_SYNC, provider);
		}
	}

	@SubscribeEvent
	public static void onAttachCapabilityItemEvent(AttachCapabilitiesEvent<ItemStack> event) {
		ItemStack stack = event.getObject();
		if (stack.getItem() instanceof IItemInventory) {
			ICapabilityProvider provider = ((IItemInventory) stack.getItem()).createInventoryProvider();
			event.addCapability(KEY_INVENTORY, provider);
		}
		if (stack.getItem() instanceof IItemTank) {
			ICapabilityProvider provider = ((IItemTank) stack.getItem()).createFluidProvider();
			event.addCapability(KEY_FLUID, provider);
		}
	}

}