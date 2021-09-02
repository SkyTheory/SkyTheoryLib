package skytheory.lib.util;

import javax.annotation.Nonnegative;

public class UpdateFrequency {

	public final int freq;
	private int count;

	public UpdateFrequency(@Nonnegative int freq) {
		if (freq <= 0) throw new IllegalArgumentException("Frequency must be positive.");
		this.freq = freq;
		this.count = freq;
	}

	public boolean shouldUpdate() {
		if (count++ >= freq) {
			count = 0;
			return true;
		}
		return false;
	}

	public boolean shouldUpdateContinuable() {
		if (count++ >= freq) {
			count = freq;
			return true;
		}
		return false;
	}

	public int getCount() {
		return count;
	}

	public void reset() {
		this.count = 0;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
