package skytheory.lib.event;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import skytheory.lib.block.LeftClickReceiverBlockEntity;
import skytheory.lib.block.RightClickReceiverBlockEntity;
import skytheory.lib.item.LeftClickReceiverItem;

public class SkyTheoryLibClickEvent {

	@SubscribeEvent
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		if (event.getItemStack().getItem() instanceof LeftClickReceiverItem item) {
			item.onLeftClickBlock(event, event.getEntity(), event.getLevel(), event.getHand(), event.getPos(), event.getFace(), event.getItemStack());
		}
		if (event.getLevel().getBlockEntity(event.getPos()) instanceof LeftClickReceiverBlockEntity block) {
			block.onLeftClicked(event, event.getEntity(), event.getHand(), event.getFace(), event.getItemStack());
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getLevel().getBlockEntity(event.getPos()) instanceof RightClickReceiverBlockEntity block) {
			block.onRightClicked(event, event.getEntity(), event.getHand(), event.getFace(), event.getItemStack());
		}
	}

	@SubscribeEvent
	public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
		if (event.getItemStack().getItem() instanceof LeftClickReceiverItem item) {
			item.onLeftClickEmpty(event, event.getEntity(), event.getLevel(), event.getHand(), event.getItemStack());
		}
	}

}
