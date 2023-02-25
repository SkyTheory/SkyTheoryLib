package skytheory.lib.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface InteractiveEntity {

	/**
	 * Entityを右クリックした際に呼ばれる
	 * @param player
	 * @param itemStack
	 * @param blockHitResult
	 * @return consume
	 */
	public void onRightClicked(PlayerInteractEvent.EntityInteract event, Player player, Level level, InteractionHand hand, ItemStack stack);
	
}
