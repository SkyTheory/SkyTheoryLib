package skytheory.lib.capability.itemhandler;

import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.capability.CapabilityHolder;
import skytheory.lib.capability.DataProvider;

/**
 * IItemHandlerを付与するEntity及びBlockEntityに実装するinterface
 * 運用時に適宜getItemHandlerを呼び出して実体を取得する
 * @author SkyTheory
 *
 */
public interface InventoryHolder<T extends Tag> extends CapabilityHolder {

	public static final ResourceLocation ITEM_HANDLER_KEY = new ResourceLocation(SkyTheoryLib.MODID, "item");

	IItemHandler getItemHandler(Direction direction);
	INBTSerializable<T> getItemHandlerSerializer();

	@Override
	default List<CapabilityEntry> getCapabilityProviders() {
		return List.of(new CapabilityEntry(ITEM_HANDLER_KEY, new DataProvider<>(ForgeCapabilities.ITEM_HANDLER, this::getItemHandler, this::getItemHandlerSerializer)));
	}
}
