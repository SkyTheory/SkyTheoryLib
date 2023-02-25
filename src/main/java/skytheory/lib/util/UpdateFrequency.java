package skytheory.lib.util;

public class UpdateFrequency {

	private int count;
	private int freq;
	
	public UpdateFrequency(int amount) {
		this.freq = amount;
	}
	
	public boolean shouldUpdate() {
		if (++count >= freq) {
			count = 0;
			return true;
		}
		return false;
	}
	
}
