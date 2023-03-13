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

	public MultiDataSerializer(List<?> handlers) {
		this.seriarizers = handlers.stream()
				.filter(INBTSerializable.class::isInstance)
				.map(INBTSerializable.class::cast)
				.map(CapabilityUtils::adjustSerilizer)
				.toList();
		if (this.seriarizers.size() != handlers.size()) {
			LogUtils.getLogger().warn("Serializer was created from an Object that does not implement INBTSerializable. Some data will not be saved.");
		}
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
			.limit(Math.min(nbt.size(), seriarizers.size()))
			.map(CompoundTag.class::cast)
			.forEach(it.next()::deserializeNBT);
			if (nbt.size() != seriarizers.size()) {
				LogUtils.getLogger().error("Deserialization maybe failed; Tags and handlers count are different.");
			}
		} else if (nbt.getElementType() != 0) {
			LogUtils.getLogger().error("Deserialization skipped; NBT type is invalid.");
		}
	}

}
