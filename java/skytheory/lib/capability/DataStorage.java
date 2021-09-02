package skytheory.lib.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.INBTSerializable;
import skytheory.lib.SkyTheoryLib;

/**
 * IStorageを実装するクラス<br>
 * Capabilityの実体からNBTへの読み書きを担う<br>
 *
 * @author SkyTheory
 *
 */
public class DataStorage<T> implements IStorage<T> {

	@SuppressWarnings("rawtypes")
	@Override
	public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
		if (instance instanceof INBTSerializable) {
			return ((INBTSerializable) instance).serializeNBT();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
		if (instance instanceof INBTSerializable) {
			try {
				INBTSerializable serializable = (INBTSerializable) instance;
				serializable.deserializeNBT(nbt);
			} catch (ClassCastException e) {
				SkyTheoryLib.LOGGER.error("Deserialize failed.");
				SkyTheoryLib.LOGGER.error(e);
			}
		}
	}
}
