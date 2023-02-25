package skytheory.lib.capability;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import skytheory.lib.util.CapabilityUtils;

public class DataProvider<T extends Tag> implements ICapabilitySerializable<T> {

	private final Capability<?> cap;
	private final Function<Direction, ?> function;
	private final INBTSerializable<T> serializer;

	public static <T extends Tag, R> DataProvider<T> createSingle(Capability<R> cap, R contents, INBTSerializable<T> serializer) {
		return new DataProvider<T>(cap, (side -> contents), serializer);
	}

	public static <T extends Tag, R> DataProvider<T> createSided(Capability<R> cap, Function<Direction, R> func, INBTSerializable<T> serializer) {
		return new DataProvider<T>(cap, func, serializer);
	}

	protected <R> DataProvider(Capability<R> cap, Function<Direction, R> function, INBTSerializable<T> serializer) {
		this.cap = cap;
		this.function = function;
		this.serializer = serializer;
	}

	@Override
	public <R> @NotNull LazyOptional<R> getCapability(@NotNull Capability<R> cap, @Nullable Direction side) {
		return this.cap.orEmpty(cap, CapabilityUtils.fromNullable(function.apply(side)));
	}

	@Override
	public T serializeNBT() {
		return serializer.serializeNBT();
	}

	@Override
	public void deserializeNBT(T nbt) {
		serializer.deserializeNBT(nbt);
	}

}
