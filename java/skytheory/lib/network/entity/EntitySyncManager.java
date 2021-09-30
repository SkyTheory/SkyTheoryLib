package skytheory.lib.network.entity;

import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.network.CapsSyncManager;
import skytheory.lib.network.PacketHandler;

@SideOnly(Side.CLIENT)
public class EntitySyncManager {

	/*
	 * Client -> Server
	 * サーバーにリクエストを送り、データを送り返してもらう
	 */
	static void request(Entity entity, Capability<?> cap, Set<EnumFacing> facings) {
		PacketHandler.CHANNEL.sendToServer(new EntitySyncRequest(entity, cap, facings));
	}

	/*
	 * Server -> Client
	 * サーバーから受け取ったデータをEntityに同期する
	 */
	static <T> void process(Entity entity, Capability<T> cap, NBTTagCompound compound) {
		CapsSyncManager.sync(entity, cap, compound);
		SkyTheoryLib.LOGGER.trace("Sync success: " + entity.getEntityId());
	}

	public static void failed(int entityId) {
		SkyTheoryLib.LOGGER.trace("Sync failed: " + entityId);
	}

}
