package skytheory.lib.util;

import static net.minecraft.core.Direction.*;

import org.jetbrains.annotations.Nullable;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class CapabilityUtils {

	public static final Direction[] DIRECTIONS = {null, DOWN, UP, NORTH, SOUTH, WEST, EAST};

	/**
	 *  読み書きを何もしない、ダミーのシリアライザ
	 */
	public static final INBTSerializable<CompoundTag> DUMMY_SERIALIZER = new INBTSerializable<CompoundTag>() {
		@Override
		public CompoundTag serializeNBT() {
			return new CompoundTag();
		}
		@Override
		public void deserializeNBT(CompoundTag nbt) {
		}
	};

	/**
	 * 任意の型のINBTSerializableをwrapし、CompoundTag型のシリアライザを作成する
	 * @param serializer
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static INBTSerializable<CompoundTag> adjustSerializer(INBTSerializable serealizer) {
		return new INBTSerializable<CompoundTag>() {

			@Override
			public CompoundTag serializeNBT() {
				CompoundTag nbt = new CompoundTag();
					nbt.put("data", serealizer.serializeNBT());
				return nbt;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void deserializeNBT(CompoundTag nbt) {
				Tag tag = nbt.get("data");
				try {
					serealizer.deserializeNBT(tag);
				} catch (ClassCastException e) {
					LogUtils.getLogger().error("Deserialization failed: " + tag.getAsString(), e);
				}
			}
		};
	}

	@Nullable
	public static <T> T getCapability(Capability<T> cap, Level level, BlockPos pos) {
		return getCapability(cap, level, pos, null);
	}

	@Nullable
	public static <T> T getCapability(Capability<T> cap, Level level, BlockPos pos, Direction side) {
		return getCapability(cap, level.getBlockEntity(pos), side);
	}

	@Nullable
	public static <T> T getCapability(Capability<T> cap, @Nullable ICapabilityProvider obj) {
		return getCapability(cap, obj, null);
	}

	@Nullable
	public static <T> T getCapability(Capability<T> cap, @Nullable ICapabilityProvider obj, Direction side) {
		if (obj == null) return null;
		return obj.getCapability(cap, side).orElse(null);
	}
}