package skytheory.lib.capability.itemhandler;

import java.util.Collection;

import net.minecraftforge.items.IItemHandler;
import skytheory.lib.capability.IModifiableHandler;

public class MultiItemHandlerModifiable extends MultiItemHandler implements IModifiableHandler<IItemHandler> {

	@Override
	public Collection<IItemHandler> getDatas() {
		return this.handlers;
	}

	@Override
	public void addData(IItemHandler data) {
		this.handlers.add(data);
	}

	@Override
	public void removeData(IItemHandler data) {
		this.handlers.remove(data);
	}

}
