package skytheory.lib.capability;

import org.apache.commons.lang3.Validate;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * オブジェクトにCapabilityを付与する際などに使われるクラス<br>
 * IStorageを継承したDataStorageと共に、NBTへの読み書きにも使われる
 * @author SkyTheory
 *
 */
public class DataProvider<T> implements ICapabilityProvider, INBTSerializable<NBTBase> {

	protected final Capability<T> capability;
	protected final T accessor;
	protected final INBTSerializable<NBTBase> serializer;

	@SuppressWarnings("unchecked")
	public DataProvider(Capability<T> capability, T instance) {
		this(capability, instance, (INBTSerializable<NBTBase>) instance);
	}

	public DataProvider(Capability<T> capability, T instance, INBTSerializable<NBTBase> serializer) {
		this.capability = Validate.notNull(capability);
		this.accessor = Validate.notNull(instance);
		this.serializer = Validate.notNull(serializer);
	}

	private boolean isEqualCapability(Capability<?> capability) {
		return (capability == this.capability);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return isEqualCapability(capability);
	}

	@Override
	public <R> R getCapability(Capability<R> capability, EnumFacing facing) {
		if (isEqualCapability(capability)) return this.capability.cast(accessor);
		return null;
	}

	@Override
	public NBTBase serializeNBT() {
		return serializer.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		serializer.deserializeNBT(nbt);
	}
}
