package skytheory.lib.network.tile;

import java.util.Set;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.network.CapsSyncManager;
import skytheory.lib.util.FacingUtils;

public class TileSyncMessage implements IMessage {

	public int x;
	public int y;
	public int z;
	public int capId;
	public NBTTagCompound compound;

	public TileSyncMessage() {}

	public TileSyncMessage(TileEntity tile, Capability<?> cap, Set<EnumFacing> facings) {
		BlockPos pos = tile.getPos();
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.capId = CapsSyncManager.lookup(cap);
		this.compound = writeNBT(tile, cap, facings);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.capId = buf.readInt();
		this.compound = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(capId);
		ByteBufUtils.writeTag(buf, compound);
	}

	@SuppressWarnings("unchecked")
	private static <T> NBTTagCompound writeNBT(TileEntity tile, Capability<T> cap, Set<EnumFacing> facings) {
		NBTTagCompound compound = new NBTTagCompound();
		facings.forEach(facing -> {
			if (tile.hasCapability(cap, facing)) {
				T data = tile.getCapability(cap, facing);
				String key = FacingUtils.getName(facing);
				if (data instanceof INBTSerializable) {
					compound.setTag(key, ((INBTSerializable<NBTBase>) data).serializeNBT());
				} else {
					SkyTheoryLib.LOGGER.warn("Unsyncable Data: " + data.getClass().getName());
				}
			}
		});
		return compound;
	}
}
