package skytheory.lib.item;

import java.util.List;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

/**
 * Integer型のリストからアイテムに色を付けるためのインターフェース
 * @author SkyTheory
 *
 */
public interface SimpleListColor extends IItemColor {

	/**
	 * ここに渡したリストを元に着色を行う
	 * @return
	 */
	public List<Integer> getColors();

	@Override
	public default int colorMultiplier(ItemStack stack, int tintIndex) {
		List<Integer> list = this.getColors();
		if (tintIndex < list.size()) {
			return list.get(tintIndex);
		}
		return 0xffffff;
	}
}
