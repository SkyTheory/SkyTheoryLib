package skytheory.lib.util;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.core.Direction;

/**
 * ISidedInventoryなどの補助のために作成された列挙型<br>
 * ブロックの六面を型として持ち、ブロックの向きとアクセスする向きから面を取得できる
 * @author SkyTheory
 *
 */
public enum BlockSide {

	BOTTOM("bottom", 0, 1),
	TOP("top", 1, 0),
	FRONT("front", 2, 3),
	BACK("back", 3, 2),
	RIGHT("right", 4, 5),
	LEFT("left", 5, 4);

	private final static Map<String, BlockSide> NAMES_LOOKUP;

	static {
		Map<String, BlockSide> map = new HashMap<>();
		for (BlockSide side : BlockSide.values()) {
			map.put(side.getName(), side);
		}
		NAMES_LOOKUP = ImmutableMap.copyOf(map);
	}

	private final String name;
	private final int id;
	private final int opposite;

	private BlockSide(String name, int id, int opposite) {
		this.name = name;
		this.id = id;
		this.opposite = opposite;
	}

	public int getIndex() {
		return id;
	}

	public BlockSide getOpposite(BlockSide side) {
		return BlockSide.values()[side.opposite];
	}

	/**
	 * 第一引数にはBlockStateの持つDirection、第二引数には取得したい面を渡すこと
	 * @param blockdirection
	 * @param sidedirection
	 * @return
	 */
	public static Direction getDirection(Direction blockDirection, BlockSide side) {
		return getDirection(BlockRotation.fromDirection(blockDirection), side);
	}

	/**
	 * TileEntityなどの持つRotationから、BlockSideに対応する方位を取得する
	 * @param rotation
	 * @param side
	 * @return
	 */
	public static Direction getDirection(BlockRotation rotation, BlockSide side) {
		return rotation.getDirection(side);
	}

	/**
	 * 第一引数にはBlockStateの持つDirection、第二引数にはその面のあるDirectionを渡すこと
	 * @param blockdirection
	 * @param sidedirection
	 * @return
	 */
	public static BlockSide getSide(Direction blockDirection, Direction direction) {
		return getSide(BlockRotation.fromDirection(blockDirection), direction);
	}

	/**
	 * TileEntityなどの持つBlockRotationから、Directionに対応する面を取得する
	 * @param rotation
	 * @param direction
	 * @return
	 */
	public static BlockSide getSide(BlockRotation rotation, Direction direction) {
		return rotation.getSide(direction);
	}

	public static Map<String, BlockSide> getMap() {
		return NAMES_LOOKUP;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public BlockSide fromName(String name) {
		return NAMES_LOOKUP.get(name);
	}

}
