package skytheory.lib.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface InteractiveBlockEntityMainHand extends InteractiveBlockEntity {
	
	@Override
	default void onLeftClicked(PlayerInteractEvent.LeftClickBlock event, Player player, InteractionHand hand, @Nullable Direction face, ItemStack stack) {
		if (hand == InteractionHand.MAIN_HAND) onLeftClicked(event, player, face, stack);
	}
	
	void onLeftClicked(PlayerInteractEvent.LeftClickBlock event, Player player, @Nullable Direction face, ItemStack stack);

	@Override
	default void onRightClicked(PlayerInteractEvent.RightClickBlock event, Player player, InteractionHand hand, @Nullable Direction face, ItemStack stack) {
		if (hand == InteractionHand.MAIN_HAND) onRightClicked(event, player, face, stack);
	}

	void onRightClicked(PlayerInteractEvent.RightClickBlock event, Player player, @Nullable Direction face, ItemStack stack);
	
}
