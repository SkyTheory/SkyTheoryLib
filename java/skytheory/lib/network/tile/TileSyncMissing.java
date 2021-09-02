package skytheory.lib.network.tile;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class TileSyncMissing implements IMessage {

	public int queueId;

	public TileSyncMissing() {}

	public TileSyncMissing(int queueId) {
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
