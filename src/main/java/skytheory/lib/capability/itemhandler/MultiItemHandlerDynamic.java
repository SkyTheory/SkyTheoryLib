package skytheory.lib.capability.itemhandler;

import net.minecraftforge.items.IItemHandler;

public class MultiItemHandlerDynamic extends MultiItemHandler {

	public MultiItemHandlerDynamic(IItemHandler... handlers) {
		super(handlers);
	}

	public void addData(IItemHandler handler) {
		this.handlers.add(handler);
	}
	
	public void removeData(IItemHandler handler) {
		this.handlers.remove(handler);
	}
	
}
