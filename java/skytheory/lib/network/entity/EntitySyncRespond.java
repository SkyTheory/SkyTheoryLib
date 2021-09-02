package skytheory.lib.network.entity;

import java.util.Set;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.network.CapsSyncManager;
import skytheory.lib.util.FacingUtils;

public class EntitySyncRespond implements IMessage {

	public int queueId;
	public int entityId;
	public int capId;
	public NBTTagCompound compound;

	public EntitySyncRespond() {}

	public EntitySyncRespond(int queueID, Entity entity, Capability<?> cap, Set<EnumFacing> facings) {
		this.queueId = queueID;
		this.entityId = entity.getEntityId();
		this.capId = CapsSyncManager.lookup(cap);
		this.compound = writeNBT(entity, cap, facings);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.queueId = buf.readInt();
		this.entityId = buf.readInt();
		this.capId = buf.readInt();
		this.compound = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(queueId);
		buf.writeInt(entityId);
		buf.writeInt(capId);
		ByteBufUtils.writeTag(buf, compound);
	}

	@SuppressWarnings("unchecked")
	private static <T> NBTTagCompound writeNBT(Entity entity, Capability<T> cap, Set<EnumFacing> facings) {
		NBTTagCompound compound = new NBTTagCompound();
		facings.forEach(facing -> {
			if (entity.hasCapability(cap, facing)) {
				T data = entity.getCapability(cap, facing);
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
