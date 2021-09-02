package skytheory.lib.item;

import org.apache.commons.lang3.Validate;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemHelper {

	public static ItemBlock createItemBlock(Block block) {
		return new ItemBlock(Validate.notNull(block));
	}
}
