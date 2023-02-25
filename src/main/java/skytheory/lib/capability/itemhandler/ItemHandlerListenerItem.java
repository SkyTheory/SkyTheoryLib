package skytheory.lib.capability.itemhandler;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * 登録したItemHandlerに変更があった際に呼び出される
 * ItemHandlerHolderStaticと共に実装しておけば、当modのEventで自動で登録を行う
 * @author SkyTheory
 *
 */
public interface ItemHandlerListenerItem {

	void onItemHandlerChanged(ItemStack stack, IItemHandler handler, int slot);
	
}
