package skytheory.lib.capability.datasync;

import net.minecraft.nbt.NBTTagCompound;

/**
 * データ同期用に受け渡す値の保管用Capabilityの実体に持たせるInterface
 * writeTo / readFrom NBTではなくこちらのメソッドでデータの読み書きを行うようにすればOK
 * @author SkyTheory
 *
 */
public interface IDataSync {

	public NBTTagCompound serializeSync();
	public void deserializeSync(NBTTagCompound compound);

}
