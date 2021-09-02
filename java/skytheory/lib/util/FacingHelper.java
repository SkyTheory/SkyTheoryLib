package skytheory.lib.util;

import javax.annotation.Nullable;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import skytheory.lib.SkyTheoryLib;

/**
 * 主にEnumFacingの回転にかかわるメソッドをまとめたもの
 * それ以外のデータの読み書きなどの補助はFloatUtilsへ
 * @author SkyTheory
 *
 */
public class FacingHelper {

	@Nullable
	public static EnumFacing getFacingFromState(IBlockState state) {
		for (IProperty<?> property : state.getPropertyKeys()) {
			if (property.getValueClass() == EnumFacing.class) {
				return (EnumFacing) state.getValue(property);
			}
		}
		SkyTheoryLib.LOGGER.warn("Cannnot decrypt facing: " + state.getBlock().getLocalizedName());
		return null;
	}

	/*
	 * EnumFacingの中のrotateと同じことを行う
	 * コレだけのためにAccessTransformerを使うのもちょっとアレなので……
	 */

	public static EnumFacing rotateX(EnumFacing facing) {
		if (facing.getAxis() == EnumFacing.Axis.X) return facing;
		switch (facing) {
		case NORTH:
			return EnumFacing.DOWN;
		case DOWN:
			return EnumFacing.SOUTH;
		case SOUTH:
			return EnumFacing.UP;
		case UP:
			return EnumFacing.NORTH;
		default:
			// 覚書：まず呼ばれることはない
			throw new UnsupportedOperationException("Unable to rotation facing of " + facing);
		}
	}
	public static EnumFacing rotateY(EnumFacing facing) {
		if (facing.getAxis() == EnumFacing.Axis.Y) return facing;
		return facing.rotateY();
	}

	public static EnumFacing rotateZ(EnumFacing facing) {
		if (facing.getAxis() == EnumFacing.Axis.Z) return facing;
		switch (facing) {
		case EAST:
			return EnumFacing.DOWN;
		case DOWN:
			return EnumFacing.WEST;
		case WEST:
			return EnumFacing.UP;
		case UP:
			return EnumFacing.EAST;
		default:
			// 覚書：まず呼ばれることはない
			throw new UnsupportedOperationException("Unable to rotation facing of " + facing);
		}
	}


	public static EnumFacing rotateXCCW(EnumFacing facing) {
		return rotateX(facing).getOpposite();
	}

	public static EnumFacing rotateYCCW(EnumFacing facing) {
		if (facing.getAxis() == EnumFacing.Axis.Y) return facing;
		return facing.rotateYCCW();
	}

	public static EnumFacing rotateZCCW(EnumFacing facing) {
		if (facing.getAxis() == EnumFacing.Axis.Z) return facing;
		return rotateZ(facing).getOpposite();
	}


	public static EnumFacing invert(EnumFacing facing) {
		return facing.getOpposite();
	}

	/**
	 * 与えられた軸に応じて回転させる
	 * EnumFacing.rotateAroundとの違いは第二引数の面から見て時計回りへ回転させること
	 * @param facing
	 * @param axis
	 * @return
	 */
	public static EnumFacing rotate(EnumFacing facing, EnumFacing axis) {
		if (facing.getAxis() == axis.getAxis()) return facing;
		switch(axis) {
		case EAST:
			return rotateX(facing);
		case WEST:
			return rotateXCCW(facing);
		case UP:
			return rotateY(facing);
		case DOWN:
			return rotateYCCW(facing);
		case SOUTH:
			return rotateZ(facing);
		case NORTH:
			return rotateZCCW(facing);
		default:
			// 覚書：まず呼ばれることはない
			throw new UnsupportedOperationException("Unable to rotation facing of " + facing);
		}
	}

	public static EnumFacing rotateCCW(EnumFacing facing, EnumFacing axis) {
		if (facing.getAxis() != axis.getAxis()) return invert(rotate(facing, axis));
		return facing;
	}

	public static EnumFacing rotateOrInvert(EnumFacing facing, EnumFacing axis) {
		if (facing.getAxis() == axis.getAxis()) return invert(facing);
		return rotate(facing, axis);
	}

	public static EnumFacing rotateCCWorInvert(EnumFacing facing, EnumFacing axis) {
		return rotate(facing, axis).getOpposite();
	}

}
