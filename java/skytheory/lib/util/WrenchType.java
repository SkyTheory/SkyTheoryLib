package skytheory.lib.util;

public abstract class WrenchType implements IWrenchType {

	public final String name;

	public WrenchType(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
