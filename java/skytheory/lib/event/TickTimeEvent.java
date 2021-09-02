package skytheory.lib.event;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import skytheory.lib.network.entity.EntitySyncManager;
import skytheory.lib.network.tile.TileSyncManager;
import skytheory.lib.util.TickHolder;

public class TickTimeEvent {


	@SubscribeEvent
	public static void renderTick(RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			TickHolder.renderTick = event.renderTickTime;
		}
	}

	@SubscribeEvent
	public static void clientTick(ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			TickHolder.clientTicks++;
			EntitySyncManager.processQueue();
			TileSyncManager.processQueue();
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			if (Objects.isNull(gui) || !gui.doesGuiPauseGame()) {
				TickHolder.inGameClientTicks++;
			}
		}
	}

	@SubscribeEvent
	public static void serverTick(ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			TickHolder.serverTicks++;
		}
	}
}