package skytheory.lib.network.tile;

import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.network.CapsSyncManager;
import skytheory.lib.network.PacketHandler;

public class TileSyncManager {

	/*
	 * Client -> Server
	 * サーバーにリクエストを送り、データを送り返してもらう
	 */
	static void request(TileEntity tile, Capability<?> cap, Set<EnumFacing> facings) {
		PacketHandler.CHANNEL.sendToServer(new TileSyncRequest(tile, cap, facings));
	}

	/*
	 * Server -> Client
	 * サーバーから受け取ったデータをEntityに同期する
	 */
	static <T> void process(TileEntity tile, Capability<T> cap, NBTTagCompound compound) {
		CapsSyncManager.sync(tile, cap, compound);
		SkyTheoryLib.LOGGER.trace("Sync success: " + tile.getPos().toString());
	}

	static void failed(BlockPos pos) {
		SkyTheoryLib.LOGGER.trace("Sync failed: " + pos.toString());
	}

}
