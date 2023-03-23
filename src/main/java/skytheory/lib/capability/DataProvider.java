package skytheory.lib.capability;

import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class DataProvider<T, U extends Tag> implements ICapabilitySerializable<U> {

	private final Capability<T> cap;
	private final Function<Direction, T> dataGetter;
	private final Supplier<INBTSerializable<U>> serializerGetter;

	public DataProvider(Capability<T> cap, Supplier<T> dataGetter, Supplier<INBTSerializable<U>> serializerGetter) {
		this.cap = cap;
		this.dataGetter = (side) -> dataGetter.get();
		this.serializerGetter = serializerGetter;
	}

	public DataProvider(Capability<T> cap, Function<Direction, T> dataGetter, Supplier<INBTSerializable<U>> serializerGetter) {
		this.cap = cap;
		this.dataGetter = dataGetter;
		this.serializerGetter = serializerGetter;
	}

	@Override
	public <R> @NotNull LazyOptional<R> getCapability(@NotNull Capability<R> cap, @Nullable Direction side) {
		return this.cap.orEmpty(cap, fromNullable(dataGetter.apply(side)));
	}

	@Override
	public U serializeNBT() {
		return serializerGetter.get().serializeNBT();
	}

	@Override
	public void deserializeNBT(U nbt) {
		serializerGetter.get().deserializeNBT(nbt);
	}

	/**
	 * Nullableな値からLazyOptionalを作成する
	 * @param object
	 * @return
	 */
	public LazyOptional<T> fromNullable(Object value) {
		if (value == null) return LazyOptional.empty();
		return LazyOptional.of(() -> value).cast();
	}
	
}
