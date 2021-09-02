package skytheory.lib.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * アイテムに色を付けるためのインターフェース<br>
 * レジストリに登録した要素から色を取得する<br>
 * レジストリに登録するクラスの方にもIItemColorの実装を要求する
 * @author SkyTheory
 *
 * @param <T>
 */
public interface SimpleAttributeColor <T extends IForgeRegistryEntry<T> & IItemColor> extends IItemColor {

	public T getAttribute(ItemStack stack);

	@Override
	public default int colorMultiplier(ItemStack stack, int tintIndex) {
		T attribute = this.getAttribute(stack);
		if (attribute != null) {
			return attribute.colorMultiplier(stack, tintIndex);

		}
		return 0xffffff;
	}

}
