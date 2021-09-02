package skytheory.lib.network.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.network.CapsSyncManager;
import skytheory.lib.network.PacketHandler;
import skytheory.lib.util.UpdateFrequency;

@SideOnly(Side.CLIENT)
public class EntitySyncManager {

	private static final Map<Integer, EntitySyncEntry> SYNC_QUEUE = new HashMap<>();

	private static int QUEUE_NUMBER = 0;
	/**
	 * 同期に失敗した場合に再同期させるまでの時間
	 */
	private static final int SYNC_FREQUENCY = 10;

	/*
	 * 覚書：クライアント側で呼ぶこと
	 */
	public static void enqueue(Entity entity, Capability<?> cap, Set<EnumFacing> facings) {
		SkyTheoryLib.LOGGER.trace("Enqueue sync: " + QUEUE_NUMBER);
		SYNC_QUEUE.put(Integer.valueOf(QUEUE_NUMBER++), new EntitySyncEntry(entity, cap, new HashSet<EnumFacing>(facings)));
	}

	/*
	 * 覚書：ClientTickTimeEventなどでクライアント側で呼ぶこと
	 * ConcurrentModificationExceptionを避けるためにWrapしている
	 * SYNC_FREQUENCYの間にDequeueが間に合わないと再度リクエストを送るが、遅延が余程大きくない限りは問題ないだろう
	 * あまりに酷いようならsynchronizedの付与を検討すること
	 * でもあれ、パフォーマンス落ちるらしいんだよねえ……
	 */
	public static void processQueue() {
		Map<Integer, EntitySyncEntry> entries = new HashMap<>(SYNC_QUEUE);
		entries.forEach((id, entry) -> {
			process(id, entry);
		});
	}

	/*
	 * Client -> Server
	 * サーバーにリクエストを送り、データを送り返してもらう
	 */
	public static void process(int queueId, EntitySyncEntry entry) {
		if (entry.freq.shouldUpdate()) {
			Entity entity = entry.entity;
			if (!entity.isAddedToWorld()) {
				failed(queueId);
			}
			PacketHandler.CHANNEL.sendToServer(new EntitySyncRequest(queueId, entity, entry.cap, entry.facings));
		}
	}

	/*
	 * Server -> Client
	 * サーバーから受け取ったデータをEntityに同期する
	 */
	public static <T> void process(int queueId, Entity entity, Capability<T> cap, NBTTagCompound compound) {
		CapsSyncManager.sync(entity, cap, compound);
		SkyTheoryLib.LOGGER.trace("Sync success: " + queueId);
		dequeue(queueId);
	}

	public static void failed(int queueId) {
		SkyTheoryLib.LOGGER.trace("Sync failed: " + queueId);
		dequeue(queueId);
	}

	private static void dequeue(int queueId) {
		SkyTheoryLib.LOGGER.trace("Dequeue sync: " + queueId);
		SYNC_QUEUE.remove(Integer.valueOf(queueId));
	}

	private static class EntitySyncEntry {

		private final Entity entity;
		private final Capability<?> cap;
		private final Set<EnumFacing> facings;
		private final UpdateFrequency freq;

		private EntitySyncEntry(Entity entity, Capability<?> cap, Set<EnumFacing> facings) {
			this.entity = Validate.notNull(entity);
			this.cap = Validate.notNull(cap);
			this.facings = Validate.notEmpty(Validate.notNull(facings));
			this.freq = new UpdateFrequency(SYNC_FREQUENCY);
		}
	}
}
