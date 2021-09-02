package skytheory.lib.network.tile;

import java.util.Set;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import skytheory.lib.network.CapsSyncManager;
import skytheory.lib.util.FacingUtils;

public class TileSyncRequest implements IMessage {

	public int queueId;
	public int x;
	public int y;
	public int z;
	public int capId;
	public int bitflag;

	public TileSyncRequest() {}

	public TileSyncRequest(int id, TileEntity tile, Capability<?> cap, Set<EnumFacing> facings) {
		this.queueId =id;
		BlockPos pos = tile.getPos();
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.capId = CapsSyncManager.lookup(cap);
		this.bitflag = FacingUtils.toBitFlags(facings);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		queueId = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		capId = buf.readInt();
		bitflag = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(queueId);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(capId);
		buf.writeInt(bitflag);
	}

}
