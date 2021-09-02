package skytheory.lib.network.entity;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class EntitySyncMissing implements IMessage {

	public int queueId;

	public EntitySyncMissing() {}

	public EntitySyncMissing(int queueId) {
		this.queueId = queueId;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.queueId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.queueId);
	}

}
