package skytheory.lib.capability.itemhandler;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.items.IItemHandler;

/**
 * IItemHandlerを付与するEntity及びBlockEntityに実装するinterface
 * 運用時に適宜getItemHandlerを呼び出して実体を取得する
 * @author SkyTheory
 *
 */
public interface ItemHandlerEntity {

	/**
	 * null sideに実装されているIItemHandlerを返す
	 * @param side
	 * @return
	 */
	@Nullable
	default IItemHandler getItemHandler() {
		return getItemHandler(null);
	}

	/**
	 * sideを引数にIItemHandlerを返す
	 * 実体の状態に応じて返すハンドラを変更することができる
	 * @param side
	 * @return
	 */
	@Nullable
	IItemHandler getItemHandler(Direction side);
	
	/**
	 * データの読み書きのために全てのIItemHandlerを取得する
	 * 実装したクラスのコンストラクタより先に呼ばれるため、
	 * ここでIItemHandlerの実体を作成すること
	 * @return
	 */
	List<IItemHandler> createAllHandlers();
	
}
