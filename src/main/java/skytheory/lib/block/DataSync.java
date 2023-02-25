package skytheory.lib.block;

import net.minecraft.nbt.CompoundTag;

public interface DataSync {

	CompoundTag writeSyncTag();
	void readSyncTag(CompoundTag tag);
	
}
