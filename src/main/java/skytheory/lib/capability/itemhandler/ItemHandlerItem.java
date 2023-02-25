package skytheory.lib.capability.itemhandler;

import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

/**
 * IItemHandlerを付与するItemStackのItemに実装するinterface
 * ItemStack生成時に作成したHandlerに対してアクセスを行う
 * @author SkyTheory
 *
 */
public interface ItemHandlerItem {

	/**
	 * ItemStackの持つIItemHandlerを取得する
	 * @param stack
	 * @return
	 */
	public static IItemHandler getItemHandler(ItemStack stack) {
		return getItemHandler(stack, null);
	}
	
	/**
	 * ItemStackの持つIItemHandlerを取得する
	 * @param stack
	 * @param side
	 * @return
	 */
	public static IItemHandler getItemHandler(ItemStack stack, Direction side) {
		return stack.getCapability(ForgeCapabilities.ITEM_HANDLER, side).orElse(null);
	}
	
	/**
	 * データの読み書きのために全てのIItemHandlerを取得する
	 * @return
	 */
	Map<Direction, IItemHandler> createAllHandlers();
	
}
