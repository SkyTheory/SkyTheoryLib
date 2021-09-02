package skytheory.lib.network;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.util.FacingUtils;

public class CapsSyncManager {

	private static final List<Capability<?>> CAPS_LOOK_UP = new ArrayList<>();

	/**
	 * Capabilityの同期をしたい場合、予めこれを呼ぶ
	 * サーバー側とクライアント側で同じ順番で呼ばれなければならない点には注意！
	 */
	public static void registerLookUp(Capability<?> cap) {
		Validate.notNull(cap);
		if (CAPS_LOOK_UP.contains(cap)) {
			// ひとつのmodに対して複数のアドオンを作成した際に競合の原因になりそうだったので何もしない
//			throw new RuntimeException("Capability look-up already registerd: " + cap.getName());
		} else {
			CAPS_LOOK_UP.add(cap);
			SkyTheoryLib.LOGGER.trace("Register look-up list: " + cap.getName());
		}
	}

	public static int lookup(Capability<?> cap) {
		int index = CAPS_LOOK_UP.indexOf(cap);
		if (index == -1) throw new IllegalStateException("Not registered capability: " + cap.getName());
		return index;
	}

	public static Capability<?> lookup(int capID) {
		return Validate.notNull(CAPS_LOOK_UP.get(capID));
	}

	/**
	 * 各種SyncManagerから呼び出し、受け取ったmessageを基に対象の同期をとる
	 * @param <T>
	 * @param target
	 * @param cap
	 * @param message
	 */
	@SuppressWarnings("unchecked")
	public static <T> void sync(ICapabilityProvider target, Capability<T> cap, NBTTagCompound message) {
		message.getKeySet().forEach(key -> {
			EnumFacing facing = FacingUtils.fromNameStrict(key);
			if (target.hasCapability(cap, facing)) {
				T data = target.getCapability(cap, facing);
				if (data instanceof INBTSerializable) {
					try {
						((INBTSerializable<NBTBase>) data).deserializeNBT(message.getTag(key));
					} catch (ClassCastException e) {
						SkyTheoryLib.LOGGER.error("Deserialize Failed");
						e.printStackTrace();
					}
				} else {
					throw new IllegalStateException("Illegal Sync Data");
				}
			}
		});
	}

}
