package skytheory.lib.util;

import net.minecraft.util.RandomSource;

public class RandomHelper {

	/**
	 * 0から指定した大きさまでの乱数を取得する
	 * @param source
	 * @param range
	 * @return random number
	 */
	public static float range(RandomSource source, float range) {
		return source.nextFloat() * range;
	}

	/**
	 * 指定した大きさの負数から正数までの乱数を取得する
	 * @param source
	 * @param range
	 * @return
	 */
	public static float rangeSigned(RandomSource source, float range) {
		return (source.nextFloat() * 2 - 1.0f) * range;
	}
	
	/**
	 * 0から指定した大きさまでの乱数を取得する
	 * @param source
	 * @param range
	 * @return
	 */
	public static double range(RandomSource source, double range) {
		return source.nextDouble() * range;
	}

	/**
	 * 指定した大きさの負数から正数までの乱数を取得する
	 * @param source
	 * @param range
	 * @return random number
	 */
	public static double rangeSigned(RandomSource source, double range) {
		return (source.nextDouble() * 2 - 1.0d) * range;
	}
	
}
