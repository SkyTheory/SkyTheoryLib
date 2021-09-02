package skytheory.lib.util;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

/**
 * 六面体のすべての向きの列挙型<br>
 * 名前は FRONT_BOTTOM_RIGHTの順<br>
 * つまり正面が南に、底面が下に、右側面が東ならSOUTH_DOWN_EASTになる
 * @author SkyTheory
 *
 */
public enum EnumRotation implements IStringSerializable {

	/*
	 * 余談：メタデータで表現するには数値が足りないため、大体はTileEntityでの実装になる
	 * SidedProviderなどを用いる際に、EnumSideとセットでどうぞ
	 */

	DOWN_NORTH_EAST(EnumFacing.DOWN, EnumFacing.NORTH),
	DOWN_SOUTH_WEST(EnumFacing.DOWN, EnumFacing.SOUTH),
	DOWN_WEST_NORTH(EnumFacing.DOWN, EnumFacing.WEST),
	DOWN_EAST_SOUTH(EnumFacing.DOWN, EnumFacing.EAST),
	UP_NORTH_WEST(EnumFacing.UP, EnumFacing.NORTH),
	UP_SOUTH_EAST(EnumFacing.UP, EnumFacing.SOUTH),
	UP_WEST_SOUTH(EnumFacing.UP, EnumFacing.WEST),
	UP_EAST_NORTH(EnumFacing.UP, EnumFacing.EAST),
	NORTH_DOWN_WEST(EnumFacing.NORTH, EnumFacing.DOWN),
	NORTH_WEST_UP(EnumFacing.NORTH, EnumFacing.WEST),
	NORTH_UP_EAST(EnumFacing.NORTH, EnumFacing.UP),
	NORTH_EAST_DOWN(EnumFacing.NORTH, EnumFacing.EAST),
	SOUTH_DOWN_EAST(EnumFacing.SOUTH, EnumFacing.DOWN),
	SOUTH_WEST_DOWN(EnumFacing.SOUTH, EnumFacing.WEST),
	SOUTH_UP_WEST(EnumFacing.SOUTH, EnumFacing.UP),
	SOUTH_EAST_UP(EnumFacing.SOUTH, EnumFacing.EAST),
	WEST_DOWN_SOUTH(EnumFacing.WEST, EnumFacing.DOWN),
	WEST_NORTH_DOWN(EnumFacing.WEST, EnumFacing.NORTH),
	WEST_UP_NORTH(EnumFacing.WEST, EnumFacing.UP),
	WEST_SOUTH_UP(EnumFacing.WEST, EnumFacing.SOUTH),
	EAST_DOWN_NORTH(EnumFacing.EAST, EnumFacing.DOWN),
	EAST_NORTH_UP(EnumFacing.EAST, EnumFacing.NORTH),
	EAST_UP_SOUTH(EnumFacing.EAST, EnumFacing.UP),
	EAST_SOUTH_DOWN(EnumFacing.EAST, EnumFacing.SOUTH);

	private static final Map<String, EnumRotation> NAMES_LOOKUP;
	private static final Map<EnumRotation, Integer> ANGLES_LOOKUP;
	private static final Map<EnumFacing, Map<EnumFacing, EnumRotation>> FACING_LOOKUP;

	// 定数として記述してもいいけれど、冗長になるので……
	static {
		Map<String, EnumRotation> names = new HashMap<>();
		for (EnumRotation rotation : EnumRotation.values()) {
			names.put(rotation.getName(), rotation);
		}
		ANGLES_LOOKUP = new EnumMap<>(EnumRotation.class);
		for (EnumFacing facing : EnumFacing.values()) {
			for (int i = 0; i < 4; i++) {
				ANGLES_LOOKUP.put(EnumRotation.fromFacingAndAngle(facing, i), i);
			}
		}
		NAMES_LOOKUP = ImmutableMap.copyOf(names);
		FACING_LOOKUP = new EnumMap<>(EnumFacing.class);
		for (EnumFacing facing : EnumFacing.values()) {
			FACING_LOOKUP.put(facing, new EnumMap<>(EnumFacing.class));
		}
		for (EnumRotation rotation : EnumRotation.values()) {
			FACING_LOOKUP.get(rotation.getFacing(EnumSide.FRONT)).put(rotation.getFacing(EnumSide.BOTTOM), rotation);
		}
	}

	private String name;
	private EnumFacing front;
	private EnumFacing bottom;
	private EnumFacing right;
	private BiMap<EnumFacing, EnumSide> sides;

	private EnumRotation(EnumFacing front, EnumFacing bottom) {
		this(front, bottom, FacingHelper.rotate(front, bottom));
	}

	private EnumRotation(EnumFacing front, EnumFacing bottom, EnumFacing right) {
		// 覚書：コーディング段階で用いたフールプルーフ、値指定での回転などのメソッドを追加する場合は参考にできるはず
//		if (front.getAxis() == bottom.getAxis() || front.getAxis() == right.getAxis() || bottom.getAxis() == right.getAxis()) {
//			throw new IllegalArgumentException(String.format("Unsolvable rotation : %s, %s, %s", front.getName(), bottom.getName(), right.getName()));
//		}
		this.name = new String(front.getName() + "-" + bottom.getName() + "-" + right.getName());
		this.front = front;
		this.bottom = bottom;
		this.right = right;
		Map<EnumFacing, EnumSide> sides = new HashMap<>();
		sides.put(bottom, EnumSide.BOTTOM);
		sides.put(bottom.getOpposite(), EnumSide.TOP);
		sides.put(front, EnumSide.FRONT);
		sides.put(front.getOpposite(), EnumSide.BACK);
		sides.put(right, EnumSide.RIGHT);
		sides.put(right.getOpposite(), EnumSide.LEFT);
		this.sides = ImmutableBiMap.copyOf(sides);
	}

	public EnumRotation rotate(EnumFacing axis) {
		EnumFacing f = FacingHelper.rotate(front, axis);
		EnumFacing b = FacingHelper.rotate(bottom, axis);
		EnumFacing r = FacingHelper.rotate(right, axis);
		for (EnumRotation value : values()) {
			if (value.front == f && value.bottom == b && value.right == r) {
				return value;
			}
		}
		// 呼ばれることはないはず
		throw new IllegalStateException("Rotation Failed.");
	}

	/**
	 * 上下の向きを保ったまま、前後と左右を反転<br>
	 * EnumFacing.UPとEnumFacing.DOWNには対応しない向きなので要注意<br>
	 * EnumFacingのまま扱いたいなら素直にfacing.getOpposite()を使うこと
	 * @return Opposite direction
	 */
	public EnumRotation getOpposite() {
		for (EnumRotation value : values()) {
			if (value.front == front.getOpposite() && value.right == right.getOpposite()) {
				return value;
			}
		}
		// 呼ばれることはないはず
		throw new IllegalStateException("Invert Failed.");
	}

	public EnumSide getSide(EnumFacing facing) {
		return this.sides.get(facing);
	}

	public EnumFacing getFront() {
		return this.sides.inverse().get(EnumSide.FRONT);
	}

	public EnumFacing getFacing(EnumSide side) {
		return this.sides.inverse().get(side);
	}

	public int getAngle() {
		return ANGLES_LOOKUP.get(this);
	}

	public static EnumRotation fromFacing(EnumFacing facing) {
		switch (facing) {
		case DOWN:
			return DOWN_SOUTH_WEST;
		case UP:
			return UP_NORTH_WEST;
		case NORTH:
			return NORTH_DOWN_WEST;
		case SOUTH:
			return SOUTH_DOWN_EAST;
		case WEST:
			return WEST_DOWN_SOUTH;
		case EAST:
			return EAST_DOWN_NORTH;
		}
		throw new IllegalArgumentException("Cannot get rotation from null");
	}

	public static EnumRotation fromFacing(EnumFacing front, EnumFacing bottom) {
		EnumRotation rotation = FACING_LOOKUP.get(front).get(bottom);
		if (Objects.isNull(rotation)) {
			throw new IllegalArgumentException(String.format("Unsolvable rotation : %s, %s, %s", front.getName(), bottom.getName()));
		}
		return rotation;
	}

	public static EnumRotation fromFacingAndAngle(EnumFacing facing, int angle) {
		EnumRotation rotation = EnumRotation.fromFacing(facing);
		int rot = angle;
		for (int i = 0; i < rot; i++) {
			rotation = rotation.rotate(rotation.getFacing(EnumSide.FRONT));
		}
		return rotation;
	}

	public static Map<String, EnumRotation> getMap() {
		return NAMES_LOOKUP;
	}

	public static EnumRotation fromName(String name) {
		return NAMES_LOOKUP.get(name);
	}

	@Override
	public String getName() {
		return name;
	}

}
