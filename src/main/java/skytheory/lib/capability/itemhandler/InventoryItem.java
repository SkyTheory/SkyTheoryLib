package skytheory.lib.capability.itemhandler;

import java.util.List;

import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.capability.CapabilityHolder;
import skytheory.lib.capability.DataProvider;

/**
 * IItemHandlerを付与するItemStackに実装するinterface
 * @author SkyTheory
 *
 */
public interface InventoryItem<T extends IItemHandler & INBTSerializable<U>, U extends Tag> extends CapabilityHolder {

	public static final ResourceLocation ITEM_HANDLER_KEY = new ResourceLocation(SkyTheoryLib.MODID, "item");

	T createItemHandler();

	@Override
	default List<CapabilityEntry> getCapabilityProviders() {
		T itemHandler = createItemHandler();
		return List.of(new CapabilityEntry(ITEM_HANDLER_KEY, new DataProvider<>(ForgeCapabilities.ITEM_HANDLER, () -> itemHandler, () -> itemHandler)));
	}
}
