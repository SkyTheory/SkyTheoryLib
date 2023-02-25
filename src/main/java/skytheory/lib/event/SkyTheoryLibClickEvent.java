package skytheory.lib.event;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import skytheory.lib.block.InteractiveBlockEntity;
import skytheory.lib.entity.InteractiveEntity;
import skytheory.lib.item.InteractiveItem;

public class SkyTheoryLibClickEvent {

	@SubscribeEvent
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		if (event.getItemStack().getItem() instanceof InteractiveItem item) {
			item.onLeftClickBlock(event, event.getEntity(), event.getLevel(), event.getHand(), event.getPos(), event.getFace(), event.getItemStack());
		}
		if (event.getLevel().getBlockEntity(event.getPos()) instanceof InteractiveBlockEntity block) {
			block.onLeftClicked(event, event.getEntity(), event.getHand(), event.getFace(), event.getItemStack());
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getItemStack().getItem() instanceof InteractiveItem item) {
			item.onRightClickBlock(event, event.getEntity(), event.getLevel(), event.getHand(), event.getPos(), event.getFace(), event.getItemStack());
		}
		if (event.getLevel().getBlockEntity(event.getPos()) instanceof InteractiveBlockEntity block) {
			block.onRightClicked(event, event.getEntity(), event.getHand(), event.getFace(), event.getItemStack());
		}
	}

	@SubscribeEvent
	public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
		if (event.getItemStack().getItem() instanceof InteractiveItem item) {
			item.onLeftClickEmpty(event, event.getEntity(), event.getLevel(), event.getHand(), event.getItemStack());
		}
	}

	@SubscribeEvent
	public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
		if (event.getItemStack().getItem() instanceof InteractiveItem item) {
			item.onRightClickEmpty(event, event.getEntity(), event.getLevel(), event.getHand(), event.getItemStack());
		}
	}

	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		if (event.getTarget() instanceof InteractiveEntity entity) {
			entity.onRightClicked(event, event.getEntity(), event.getLevel(), event.getHand(), event.getItemStack());
		}
	}

}
