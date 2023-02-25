package skytheory.lib.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface InteractiveEntityMainHand extends InteractiveEntity {

	@Override
	default void onRightClicked(PlayerInteractEvent.EntityInteract event, Player player, Level level, InteractionHand hand, ItemStack stack) {
		if (hand == InteractionHand.MAIN_HAND) onRightClicked(event, player, level, stack);
	}

	void onRightClicked(PlayerInteractEvent.EntityInteract event, Player player, Level level, ItemStack stack);
	
}
