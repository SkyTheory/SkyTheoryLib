package skytheory.lib.network.entity;

import java.util.Set;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import skytheory.lib.network.CapsSyncManager;
import skytheory.lib.util.FacingUtils;

public class EntitySyncRequest implements IMessage {

	public int entityId;
	public int capId;
	public int bitflag;

	public EntitySyncRequest() {}

	public EntitySyncRequest(Entity entity, Capability<?> cap, Set<EnumFacing> facings) {
		this.entityId = entity.getEntityId();
		this.capId = CapsSyncManager.lookup(cap);
		this.bitflag = FacingUtils.toBitFlags(facings);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityId = buf.readInt();
		capId = buf.readInt();
		bitflag = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeInt(capId);
		buf.writeInt(bitflag);
	}

}
