package skytheory.lib.item;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * 液体コンテナを持たせるアイテムに対してIItemHandlerのCapabilityを付与するためのインターフェース
 * @author SkyTheory
 *
 */
public interface IItemTank {

	public ICapabilityProvider createFluidProvider();
}
