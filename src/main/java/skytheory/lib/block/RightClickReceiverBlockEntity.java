package skytheory.lib.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface RightClickReceiverBlockEntity {
	
	/**
	 * ブロックを右クリックした際に呼ばれる
	 * @param player
	 * @param itemStack
	 * @param blockHitResult
	 */
	public void onRightClicked(PlayerInteractEvent.RightClickBlock event, Player player, InteractionHand hand, @Nullable Direction face, ItemStack stack);
	
}
