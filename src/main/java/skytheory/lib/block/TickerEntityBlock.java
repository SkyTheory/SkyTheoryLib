package skytheory.lib.block;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface TickerEntityBlock extends EntityBlock {

	default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return (level, pos, state, entity) -> {
			if (entity instanceof TickerBlockEntity ticker && entity.getType() == pBlockEntityType) {
				ticker.tick();
			}
		};
	}
	
}
