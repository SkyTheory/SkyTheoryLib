package skytheory.lib.item;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * インベントリを持たせるアイテムに対してIItemHandlerのCapabilityを付与するためのインターフェース
 * @author SkyTheory
 *
 */
public interface IItemInventory {

	public ICapabilityProvider createInventoryProvider();
}
