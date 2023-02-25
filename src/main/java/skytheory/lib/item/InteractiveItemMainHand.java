package skytheory.lib.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface InteractiveItemMainHand extends InteractiveItem {
	
	@Override
	default void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, Player player, Level level, InteractionHand hand, BlockPos pos, @Nullable Direction face, ItemStack stack) {
		if (hand == InteractionHand.MAIN_HAND) onLeftClickBlock(event, player, level, pos, face, stack);
	}
	
	void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, Player player, Level level, BlockPos pos, @Nullable Direction face, ItemStack stack);

	@Override
	default void onRightClickBlock(PlayerInteractEvent.RightClickBlock event, Player player, Level level, InteractionHand hand, BlockPos pos, @Nullable Direction face, ItemStack stack) {
		if (hand == InteractionHand.MAIN_HAND) onRightClickBlock(event, player, level, pos, face, stack);
	}

	void onRightClickBlock(PlayerInteractEvent.RightClickBlock event, Player player, Level level, BlockPos pos, @Nullable Direction face, ItemStack stack);
	
	@Override
	default void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event, Player player, Level level, InteractionHand hand, ItemStack stack) {
		if (hand == InteractionHand.MAIN_HAND) onLeftClickEmpty(event, player, level, stack);
	}

	void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event, Player player, Level level, ItemStack stack);
	
	@Override
	default void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event, Player player, Level level, InteractionHand hand, ItemStack stack) {
		if (hand == InteractionHand.MAIN_HAND) onRightClickEmpty(event, player, level, stack);
	}

	void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event, Player player, Level level, ItemStack stack);
	
}
