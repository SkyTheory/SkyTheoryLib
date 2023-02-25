package skytheory.lib.block;

import net.minecraft.world.item.Item;

/**
 * ResourceRegisterでの登録時にBlockがこれを継承していれば、対応するItemの自動生成時に指定したItemでの登録を行う
 * @author SkyTheory
 *
 */
public interface BlockItemSpecifier {

	public Item createItem();
	
}
