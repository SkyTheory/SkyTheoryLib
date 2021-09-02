package skytheory.lib.capability;

import java.util.Collection;

public interface IModifiableHandler<T> {

	public Collection<T> getDatas();
	public void addData(T data);
	public void removeData(T data);
}
