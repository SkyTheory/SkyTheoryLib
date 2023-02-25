package skytheory.lib.util;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.joml.Quaternionf;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

/**
 * 六面体のすべての向きの列挙型<br>
 * @author SkyTheory
 *
 */
public enum BlockRotation implements StringRepresentable {

	DOWN_0("down_0", Direction.DOWN, Direction.SOUTH),
	DOWN_1("down_1", Direction.DOWN, Direction.EAST),
	DOWN_2("down_2", Direction.DOWN, Direction.NORTH),
	DOWN_3("down_3", Direction.DOWN, Direction.WEST),

	UP_0("up_0", Direction.UP, Direction.NORTH),
	UP_1("up_1", Direction.UP, Direction.EAST),
	UP_2("up_2", Direction.UP, Direction.SOUTH),
	UP_3("up_3", Direction.UP, Direction.WEST),

	NORTH_0("north_0", Direction.NORTH, Direction.DOWN),
	NORTH_1("north_1", Direction.NORTH, Direction.WEST),
	NORTH_2("north_2", Direction.NORTH, Direction.UP),
	NORTH_3("north_3", Direction.NORTH, Direction.EAST),

	SOUTH_0("south_0", Direction.SOUTH, Direction.DOWN),
	SOUTH_1("south_1", Direction.SOUTH, Direction.EAST),
	SOUTH_2("south_2", Direction.SOUTH, Direction.UP),
	SOUTH_3("south_3", Direction.SOUTH, Direction.WEST),

	WEST_0("west_0", Direction.WEST, Direction.DOWN),
	WEST_1("west_1", Direction.WEST, Direction.NORTH),
	WEST_2("west_2", Direction.WEST, Direction.UP),
	WEST_3("west_3", Direction.WEST, Direction.SOUTH),

	EAST_0("east_0", Direction.EAST, Direction.DOWN),
	EAST_1("east_1", Direction.EAST, Direction.SOUTH),
	EAST_2("east_2", Direction.EAST, Direction.UP),
	EAST_3("east_3", Direction.EAST, Direction.NORTH);

	private static final Map<String, BlockRotation> NAMES_LOOKUP;
	private static final Map<BlockRotation, Integer> ANGLES_LOOKUP;
	private static final Map<Direction, Map<Direction, BlockRotation>> DIRECTION_LOOKUP;

	// 定数として記述してもいいけれど、冗長になるので……
	static {
		Map<String, BlockRotation> names = new HashMap<>();
		for (BlockRotation rotation : BlockRotation.values()) {
			names.put(rotation.getName(), rotation);
		}
		ANGLES_LOOKUP = new EnumMap<>(BlockRotation.class);
		for (Direction direction : Direction.values()) {
			for (int i = 0; i < 4; i++) {
				ANGLES_LOOKUP.put(BlockRotation.fromDirectionAndAngle(direction, i), i);
			}
		}
		NAMES_LOOKUP = ImmutableMap.copyOf(names);
		DIRECTION_LOOKUP = new EnumMap<>(Direction.class);
		for (Direction direction : Direction.values()) {
			DIRECTION_LOOKUP.put(direction, new EnumMap<>(Direction.class));
		}
		for (BlockRotation rotation : BlockRotation.values()) {
			DIRECTION_LOOKUP.get(rotation.getDirection(BlockSide.FRONT)).put(rotation.getDirection(BlockSide.BOTTOM), rotation);
		}
	}

	private String name;
	private Direction front;
	private Direction bottom;
	private Direction right;
	private BiMap<Direction, BlockSide> sides;

	private BlockRotation(String name, Direction front, Direction bottom) {
		this(name, front, bottom, DirectionUtils.getClockWise(front, bottom));
	}

	private BlockRotation(String name, Direction front, Direction bottom, Direction right) {
		this.name = name;
		this.front = front;
		this.bottom = bottom;
		this.right = right;
		Map<Direction, BlockSide> sides = new HashMap<>();
		sides.put(bottom, BlockSide.BOTTOM);
		sides.put(bottom.getOpposite(), BlockSide.TOP);
		sides.put(front, BlockSide.FRONT);
		sides.put(front.getOpposite(), BlockSide.BACK);
		sides.put(right, BlockSide.RIGHT);
		sides.put(right.getOpposite(), BlockSide.LEFT);
		this.sides = ImmutableBiMap.copyOf(sides);
	}

	public BlockRotation rotate(Direction axis) {
		Direction f = DirectionUtils.getClockWise(front, axis);
		Direction b = DirectionUtils.getClockWise(bottom, axis);
		Direction r = DirectionUtils.getClockWise(right, axis);
		for (BlockRotation value : values()) {
			if (value.front == f && value.bottom == b && value.right == r) {
				return value;
			}
		}
		// 呼ばれることはないはず
		throw new IllegalStateException("Rotation Failed.");
	}

	/**
	 * 上下の向きを保ったまま、前後と左右を反転<br>
	 * Direction.UPとDirection.DOWNには対応しない向きなので要注意<br>
	 * Directionのまま扱いたいなら素直にdirection.getOpposite()を使うこと
	 * @return Opposite direction
	 */
	public BlockRotation getOpposite() {
		for (BlockRotation value : values()) {
			if (value.front == front.getOpposite() && value.right == right.getOpposite()) {
				return value;
			}
		}
		// 呼ばれることはないはず
		throw new IllegalStateException("Invert Failed.");
	}

	public BlockSide getSide(Direction direction) {
		return this.sides.get(direction);
	}

	public Direction getFront() {
		return this.sides.inverse().get(BlockSide.FRONT);
	}

	public Direction getDirection(BlockSide side) {
		return this.sides.inverse().get(side);
	}

	/**
	 * NORTH ベースでQuaternionを取得する
	 * @return
	 */
	public Quaternionf getQuaternion() {
		Quaternionf q1 = switch (this.front) {
		case DOWN: {
			yield new Quaternionf().rotationX((float)Math.PI / 2.0f);
		}
		case UP: {
			yield new Quaternionf().rotationX((float)Math.PI / -2.0f);
		}
		case NORTH: {
			yield new Quaternionf();
		}
		case SOUTH: {
			yield new Quaternionf().rotationY((float)Math.PI);
		}
		case EAST: {
			yield new Quaternionf().rotationY((float)Math.PI / 2.0f);
		}
		case WEST: {
			yield new Quaternionf().rotationY((float)Math.PI / -2.0f);
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + front);
		};
		Quaternionf q2 = new Quaternionf().rotateZ(ANGLES_LOOKUP.get(this) * (float) Math.PI / 2.0f);
		return q1.mul(q2);
	}

	public static BlockRotation fromDirection(Direction direction) {
		return switch (direction) {
		case DOWN:
			yield DOWN_0;
		case UP:
			yield UP_0;
		case NORTH:
			yield NORTH_0;
		case SOUTH:
			yield SOUTH_0;
		case WEST:
			yield WEST_0;
		case EAST:
			yield EAST_0;
		default:
			yield null;
		};
	}

	public static BlockRotation fromDirection(Direction front, Direction bottom) {
		BlockRotation rotation = DIRECTION_LOOKUP.get(front).get(bottom);
		if (Objects.isNull(rotation)) {
			throw new IllegalArgumentException(String.format("I think there is no exist object in Euclidean space that requirement, Front: %s, Bottom: %s.", front.getName(), bottom.getName()));
		}
		return rotation;
	}

	public static BlockRotation fromDirectionAndAngle(Direction direction, int angle) {
		BlockRotation rotation = BlockRotation.fromDirection(direction);
		int rot = angle;
		for (int i = 0; i < rot; i++) {
			rotation = rotation.rotate(rotation.getDirection(BlockSide.FRONT));
		}
		return rotation;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return getName();
	}

	public static Optional<BlockRotation> fromName(String name) {
		return Optional.of(NAMES_LOOKUP.get(name));
	}

	@Override
	public String getSerializedName() {
		return name;
	}

}
