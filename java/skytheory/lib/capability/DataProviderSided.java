package skytheory.lib.capability;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import skytheory.lib.tile.ISidedTile;
import skytheory.lib.util.EnumSide;

public class DataProviderSided<T> implements ICapabilitySerializable<NBTBase> {

	protected final Capability<T> capability;
	protected final ISidedTile sided;
	protected final T serializerHandler;
	protected final INBTSerializable<NBTBase> serializer;
	protected final Map<EnumSide, IModifiableHandler<T>> sidedDataMap;

	/**
	 * Supplierが返す値は必ずTとIModifiableHandler<T>の両方を継承していること
	 */
	@SuppressWarnings("unchecked")
	public DataProviderSided(Capability<T> capability, ISidedTile tile, Supplier<IModifiableHandler<T>> supplier, T serializer) {
		this.capability = Validate.notNull(capability);
		this.sided = tile;
		if (serializer instanceof INBTSerializable) {
			this.serializer = (INBTSerializable<NBTBase>) serializer;
		} else {
			throw new IllegalArgumentException("Serializer must implement INBTSerializable.");
		}
		this.serializerHandler = serializer;
		Map<EnumSide, IModifiableHandler<T>> sided = new HashMap<>();
		for (EnumSide side : EnumSide.values()) {
			sided.put(side, supplier.get());
		}
		this.sidedDataMap = ImmutableMap.copyOf(sided);
	}
	/**
	 * Functionが返す値は必ずTとIModifiableHandler<T>の両方を継承していること
	 */
	@SuppressWarnings("unchecked")
	public DataProviderSided(Capability<T> capability, ISidedTile tile, Function<EnumSide, IModifiableHandler<T>> function, T serializer) {
		this.capability = Validate.notNull(capability);
		this.sided = tile;
		if (serializer instanceof INBTSerializable) {
			this.serializer = (INBTSerializable<NBTBase>) serializer;
		} else {
			throw new IllegalArgumentException("Serializer must implement INBTSerializable.");
		}
		this.serializerHandler = serializer;
		Map<EnumSide, IModifiableHandler<T>> sided = new HashMap<>();
		for (EnumSide side : EnumSide.values()) {
			sided.put(side, function.apply(side));
		}
		this.sidedDataMap = ImmutableMap.copyOf(sided);
	}

	private boolean isEqualCapability(Capability<?> capability) {
		return (this.capability == capability);
	}

	public void addDataToSide(T data, EnumSide side) {
		this.sidedDataMap.get(Validate.notNull(side)).addData(data);
	}

	public void removeDataFromSide(T data, EnumSide side) {
		this.sidedDataMap.get(Validate.notNull(side)).removeData(data);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (!this.isEqualCapability(capability)) return false;
		if (facing == null) return true;
		EnumSide side = sided.getSide(facing);
		return !this.sidedDataMap.get(side).getDatas().isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R getCapability(Capability<R> capability, EnumFacing facing) {
		if (!this.isEqualCapability(capability)) return null;
		if (facing == null) return this.capability.cast(this.serializerHandler);
		return this.capability.cast((T) this.sidedDataMap.get(this.sided.getSide(facing)));
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
