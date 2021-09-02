package skytheory.lib.util;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

/**
 * ISidedInventoryなどの補助のために作成された列挙型<br>
 * ブロックの六面を型として持ち、ブロックの向きとアクセスする向きから面を取得できる
 * @author SkyTheory
 *
 */
public enum EnumSide implements IStringSerializable {

	BOTTOM("bottom", 0, 1),
	TOP("top", 1, 0),
	FRONT("front", 2, 3),
	BACK("back", 3, 2),
	RIGHT("right", 4, 5),
	LEFT("left", 5, 4);

	private final static Map<String, EnumSide> NAMES_LOOKUP;

	static {
		Map<String, EnumSide> map = new HashMap<>();
		for (EnumSide side : EnumSide.values()) {
			map.put(side.getName(), side);
		}
		NAMES_LOOKUP = ImmutableMap.copyOf(map);
	}

	private final String name;
	private final int id;
	private final int opposite;

	private EnumSide(String name, int id, int opposite) {
		this.name = name;
		this.id = id;
		this.opposite = opposite;
	}

	public int getIndex() {
		return id;
	}

	public EnumSide getOpposite(EnumSide side) {
		return EnumSide.values()[side.opposite];
	}

	/**
	 * 第一引数にはIBlockStateの持つFacing、第二引数には取得したい面を渡すこと
	 * @param blockfacing
	 * @param sidefacing
	 * @return
	 */
	public static EnumFacing getFacing(EnumFacing blockfacing, EnumSide side) {
		return getFacing(EnumRotation.fromFacing(blockfacing), side);
	}

	/**
	 * TileEntityなどの持つEnumRotationから、EnumSideに対応する方位を取得する
	 * @param rotation
	 * @param side
	 * @return
	 */
	public static EnumFacing getFacing(EnumRotation rotation, EnumSide side) {
		return rotation.getFacing(side);
	}

	/**
	 * 第一引数にはIBlockStateの持つFacing、第二引数にはその面のあるFacingを渡すこと
	 * @param blockfacing
	 * @param sidefacing
	 * @return
	 */
	public static EnumSide getSide(EnumFacing blockfacing, EnumFacing facing) {
		return getSide(EnumRotation.fromFacing(blockfacing), facing);
	}

	/**
	 * TileEntityなどの持つEnumRotationから、EnumFacingに対応する面を取得する
	 * @param rotation
	 * @param facing
	 * @return
	 */
	public static EnumSide getSide(EnumRotation rotation, EnumFacing facing) {
		return rotation.getSide(facing);
	}

	public static Map<String, EnumSide> getMap() {
		return NAMES_LOOKUP;
	}

	public EnumSide fromName(String name) {
		return NAMES_LOOKUP.get(name);
	}

	@Override
	public String getName() {
		return name;
	}
}
