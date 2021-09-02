package skytheory.lib.capability.datasync;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * IDataSyncを継承したオブジェクトのデータを同期するために使用する橋渡し役のCapability
 * @author SkyTheory
 *
 */
public class DataSyncHandler implements INBTSerializable<NBTBase> {

	@CapabilityInject(DataSyncHandler.class)
	public static final Capability<DataSyncHandler> SYNC_DATA_CAPABILITY = null;

	private final IDataSync data;

	public DataSyncHandler(IDataSync data) {
		this.data = data;
	}

	@Override
	public NBTBase serializeNBT() {
		return this.data.serializeSync();
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			this.data.deserializeSync((NBTTagCompound) nbt);
		}
	}

}
