package skytheory.lib.util;

/**
 * モデルやレンダーの計算でfloatを扱うことが多かったので作成したクラス
 * @author SkyTheory
 *
 */
public class FloatUtils {

	/** pi/180の近似値 */
	private final static float TORADIAN = 0.017453292f;
	/** float型のPI */
	public static final float PI = (float) Math.PI;

	/** double型であればMath.toRadianが使える */
	public static float toRadian(float degree) {
		return degree * TORADIAN;
	}

	/** double型であればMath.toDegreeが使える */
	public static float toDegree(float degree) {
		return degree / TORADIAN;
	}

	/** ピクセル数で表された長さをブロック数に変換する */
	public static float pixelToBlock(float pixel) {
		return pixel / 16.0f;
	}

}
