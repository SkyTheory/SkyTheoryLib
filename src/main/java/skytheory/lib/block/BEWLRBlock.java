package skytheory.lib.block;

import java.util.function.Consumer;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import skytheory.lib.client.renderer.ModelSelector;
import skytheory.lib.client.renderer.SimpleBEWLR;

/**
 * BEWLRを用いたブロックのショートカット
 * Blockの作成時にModelSelectorでなく、こちらを継承することで、対応するItemのBEWLRへの登録を自動で行う
 * @author SkyTheory
 *
 */
public interface BEWLRBlock extends BlockItemSpecifier, ModelSelector {

	public static IClientItemExtensions extension = new IClientItemExtensions() {
		@Override
		public BlockEntityWithoutLevelRenderer getCustomRenderer() {
			return SimpleBEWLR.INSTANCE;
		}
	};
	
	/**
	 * 単純なBlockItemでよければ変更は不要
	 * 設置時に向きを設定したい、などがあればオーバーライドすること
	 */
	@Override
	public default Item createItem() {
		BlockItem item = new BlockItem((Block) this, new Item.Properties()) {
			@Override
			public void initializeClient(Consumer<IClientItemExtensions> consumer) {
				consumer.accept(BEWLRBlock.extension);
			}
		};
		return item;
	}
	
}
