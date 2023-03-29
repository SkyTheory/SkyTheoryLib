package skytheory.lib.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface LeftClickReceiverItem {

	/**
	 * ブロックを左クリックした際に呼ばれる
	 */
	public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, Player player, Level level, InteractionHand hand, BlockPos pos, @Nullable Direction face, ItemStack stack);
	
	/**
	 * 中空を左クリックした際に呼ばれる
	 */
	public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event, Player player, Level level, InteractionHand hand, ItemStack stack);

}
