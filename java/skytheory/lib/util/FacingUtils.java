package skytheory.lib.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import skytheory.lib.SkyTheoryLib;

public class FacingUtils {

	public static final String KEY_NULL_FACE = "null";
	public static final Set<EnumFacing> SET_SINGLE_NULL = Collections.singleton(null);

	/**
	 * nullを含む全ての面のセット
	 */
	public static final Set<EnumFacing> SET_ALL_FACES = new HashSet<>();
	static {
		SET_ALL_FACES.add(null);
		for (EnumFacing facing : EnumFacing.values()) {
			SET_ALL_FACES.add(facing);
		}
	}

	public static String getName(EnumFacing facing) {
		return Objects.isNull(facing) ? KEY_NULL_FACE : facing.getName();
	}

	public static EnumFacing fromName(String key) {
		if (key.equals(KEY_NULL_FACE)) return null;
		EnumFacing facing = EnumFacing.byName(key);
		if (Objects.isNull(facing)) {
			SkyTheoryLib.LOGGER.error("Invalid key for EnumFacing");
		}
		return facing;
	}

	public static EnumFacing fromNameStrict(String key) {
		if (key.equals(KEY_NULL_FACE)) return null;
		EnumFacing facing = EnumFacing.byName(key);
		if (Objects.isNull(facing)) {
			throw new IllegalArgumentException("Invalid key for EnumFacing");
		}
		return facing;
	}

	public static Set<EnumFacing> getExistsFacingSet(ICapabilityProvider provider, Capability<?> cap) {
		Set<EnumFacing> result = new HashSet<>();
		FacingUtils.SET_ALL_FACES.forEach(facing -> {
			if (provider.hasCapability(cap, facing)) {
				result.add(facing);
			}
		});
		return result;
	}

	public static int toIndex(EnumFacing facing) {
		return facing == null ? EnumFacing.VALUES.length : facing.getIndex();
	}

	public static EnumFacing fromIndex(int index) {
		if (index == EnumFacing.VALUES.length) return null;
		return EnumFacing.VALUES[index];
	}

	public static int toBitFlags(Set<EnumFacing> facings) {
		int flags = 0;
		for (EnumFacing facing : facings) {
			int flag = 1 << toIndex(facing);
			flags |= flag;
		}
		return flags;
	}

	public static Set<EnumFacing> fromBitFlags(int flags){
		Set<EnumFacing> set = new HashSet<>();
		for (int i = 0; i <= EnumFacing.VALUES.length; i++) {
			if ((flags & (1 << i)) != 0) {
				set.add(fromIndex(i));
			}
		}
		return set;
	}
}
