package skytheory.lib.util;

public enum ItemHandlerMode {

	EXECUTE(false),
	SIMULATE(true);
	
	private boolean actual;
	
	private ItemHandlerMode(boolean actual) {
		this.actual = actual;
	}
	
	public boolean actual() {
		return this.actual;
	}
	
	public static ItemHandlerMode of(boolean mode) {
		return mode ? SIMULATE : EXECUTE;
	}
}
