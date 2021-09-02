package skytheory.lib.network.tile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.network.CapsSyncManager;
import skytheory.lib.network.PacketHandler;
import skytheory.lib.util.UpdateFrequency;

public class TileSyncManager {

	private static final Map<Integer, TileSyncEntry> SYNC_QUEUE = new HashMap<>();

	private static int QUEUE_NUMBER = 0;
	/**
	 * 同期に失敗した場合に再同期させるまでの時間
	 */
	private static final int SYNC_FREQUENCY = 10;

	/*
	 * 覚書：クライアント側で呼ぶこと
	 */
	public static void enqueue(TileEntity tile, Capability<?> cap, Set<EnumFacing> facings) {
		SkyTheoryLib.LOGGER.trace("Enqueue sync: " + QUEUE_NUMBER);
		SYNC_QUEUE.put(Integer.valueOf(QUEUE_NUMBER++), new TileSyncEntry(tile, cap, new HashSet<EnumFacing>(facings)));
	}

	/*
	 * 覚書：ClientTickTimeEventなどでクライアント側で呼ぶこと
	 * ConcurrentModificationExceptionを避けるためにWrapしている
	 * SYNC_FREQUENCYの間にDequeueが間に合わないと再度リクエストを送るが、遅延が余程大きくない限りは問題ないだろう
	 * あまりに酷いようならsynchronizedの付与を検討すること
	 * でもあれ、パフォーマンス落ちるらしいんだよねえ……
	 */
	public static void processQueue() {
		Map<Integer, TileSyncEntry> entries = new HashMap<>(SYNC_QUEUE);
		entries.forEach((id, entry) -> {
			process(id, entry);
		});
	}

	/*
	 * Client -> Server
	 * サーバーにリクエストを送り、データを送り返してもらう
	 */
	public static void process(int queueId, TileSyncEntry entry) {
		if (entry.freq.shouldUpdate()) {
			TileEntity tile = entry.tile;
			if (entry.tile.getWorld().getTileEntity(tile.getPos()) != entry.tile) {
				failed(queueId);
			}
			PacketHandler.CHANNEL.sendToServer(new TileSyncRequest(queueId, tile, entry.cap, entry.facings));
		}
	}

	/*
	 * Server -> Client
	 * サーバーから受け取ったデータをEntityに同期する
	 */
	public static <T> void process(int queueId, TileEntity tile, Capability<T> cap, NBTTagCompound compound) {
		CapsSyncManager.sync(tile, cap, compound);
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

	private static class TileSyncEntry {

		private final TileEntity tile;
		private final Capability<?> cap;
		private final Set<EnumFacing> facings;
		private final UpdateFrequency freq;

		private TileSyncEntry(TileEntity tile, Capability<?> cap, Set<EnumFacing> facings) {
			this.tile = Validate.notNull(tile);
			this.cap = Validate.notNull(cap);
			this.facings = Validate.notEmpty(Validate.notNull(facings));
			this.freq = new UpdateFrequency(SYNC_FREQUENCY);
		}
	}
}
