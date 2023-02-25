package skytheory.lib.capability.itemhandler;

import net.minecraftforge.items.IItemHandler;

/**
 * 登録したItemHandlerに変更があった際に呼び出される
 * ItemHandlerHolderと共に実装しておけば、当modのEventで自動で登録を行う
 * ItemStackの場合、こちらでなくItemHandlerListenerItemの方を実装することを推奨
 * @author SkyTheory
 *
 */
public interface ItemHandlerListener {

	void onItemHandlerChanged(IItemHandler handler, int slot);
	
}
