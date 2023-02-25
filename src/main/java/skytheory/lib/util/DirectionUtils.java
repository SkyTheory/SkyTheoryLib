package skytheory.lib.util;

import java.util.Optional;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

/**
 * 主にDirectionの回転にかかわるメソッドをまとめたもの
 * @author SkyTheory
 *
 */
public class DirectionUtils {

	/*
	 * Directionの中のrotateと同じことを行う
	 * コレだけのためにAccessTransformerを使うのもちょっとアレなので……
	 */

	public static Direction getClockWiseX(Direction direction) {
		if (direction.getAxis() == Direction.Axis.X) return direction;
		switch (direction) {
		case NORTH:
			return Direction.DOWN;
		case DOWN:
			return Direction.SOUTH;
		case SOUTH:
			return Direction.UP;
		case UP:
			return Direction.NORTH;
		default:
			throw new IllegalArgumentException("Since when did the Minecraft universe become 4-dimensional? Direction:" + direction);
		}
	}
	public static Direction getClockWiseY(Direction direction) {
		if (direction.getAxis() == Direction.Axis.Y) return direction;
		return direction.getClockWise();
	}

	public static Direction getClockWiseZ(Direction direction) {
		if (direction.getAxis() == Direction.Axis.Z) return direction;
		switch (direction) {
		case EAST:
			return Direction.DOWN;
		case DOWN:
			return Direction.WEST;
		case WEST:
			return Direction.UP;
		case UP:
			return Direction.EAST;
		default:
			throw new IllegalArgumentException("Since when did the Minecraft universe become 4-dimensional? Direction:" + direction);
		}
	}


	public static Direction getCounterClockWiseX(Direction direction) {
		return getClockWiseX(direction).getOpposite();
	}

	public static Direction getCounterClockWiseY(Direction direction) {
		if (direction.getAxis() == Direction.Axis.Y) return direction;
		return direction.getCounterClockWise();
	}

	public static Direction getCounterClockWiseZ(Direction direction) {
		if (direction.getAxis() == Direction.Axis.Z) return direction;
		return getClockWiseZ(direction).getOpposite();
	}


	public static Direction invert(Direction direction) {
		return direction.getOpposite();
	}

	/**
	 * 与えられた軸に応じて回転させる
	 * Direction.getClockWiseとの違いは第二引数の面から見て時計回りへ回転させること
	 */
	public static Direction getClockWise(Direction direction, Direction axis) {
		if (direction.getAxis() == axis.getAxis()) return direction;
		switch(axis) {
		case EAST:
			return getClockWiseX(direction);
		case WEST:
			return getCounterClockWiseX(direction);
		case UP:
			return getClockWiseY(direction);
		case DOWN:
			return getCounterClockWiseY(direction);
		case SOUTH:
			return getClockWiseZ(direction);
		case NORTH:
			return getCounterClockWiseZ(direction);
		default:
			throw new IllegalArgumentException("Since when did the Minecraft universe become 4-dimensional? Direction:" + direction);
		}
	}

	public static Direction getCounterClockWise(Direction direction, Direction axis) {
		if (direction.getAxis() != axis.getAxis()) return invert(getClockWise(direction, axis));
		return direction;
	}

	public static Direction getClockWiseOrOpposite(Direction direction, Direction axis) {
		if (direction.getAxis() == axis.getAxis()) return invert(direction);
		return getClockWise(direction, axis);
	}

	public static Direction getCounterClockWiseOrOpposite(Direction direction, Direction axis) {
		return getClockWise(direction, axis).getOpposite();
	}
	
	public static Optional<DirectionProperty> getProperty(BlockState state) {
		return state.getProperties().stream()
				.filter(prop -> prop.getName() == "facing")
				.filter(DirectionProperty.class::isInstance)
				.map(DirectionProperty.class::cast)
				.findFirst();
	}
	
	public static Direction getDirection(BlockState state) {
		Optional<DirectionProperty> property = getProperty(state);
		if (property.isPresent()) {
			return state.getValue(property.get());
		}
		return null;
	}

}
