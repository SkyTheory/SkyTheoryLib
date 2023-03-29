package skytheory.lib.capability;

import java.util.Iterator;
import java.util.List;

import com.mojang.logging.LogUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import skytheory.lib.util.CapabilityUtils;

public class MultiDataSerializer implements INBTSerializable<ListTag> {

	protected final List<INBTSerializable<CompoundTag>> seriarizers; 

	public MultiDataSerializer(List<? extends INBTSerializable<?>> handlers) {
		this.seriarizers = handlers.stream()
				.map(CapabilityUtils::adjustSerializer)
				.toList();
	}

	@Override
	public ListTag serializeNBT() {
		ListTag datas = new ListTag();
		this.seriarizers.forEach(writer -> datas.add(writer.serializeNBT()));
		return datas;
	}

	@Override
	public void deserializeNBT(ListTag nbt) {
		if (nbt.getElementType() == Tag.TAG_COMPOUND) {
			Iterator<INBTSerializable<CompoundTag>> it = this.seriarizers.iterator();
			nbt.stream()
			.limit(seriarizers.size())
			.map(CompoundTag.class::cast)
			.forEach(it.next()::deserializeNBT);
			if (nbt.size() != seriarizers.size()) {
				LogUtils.getLogger().error("Deserialization maybe failed; Tags and handlers count are different.");
			}
		} else if (!nbt.isEmpty()) {
			LogUtils.getLogger().error("Deserialization skipped; NBT type is invalid.");
		}
	}

}
