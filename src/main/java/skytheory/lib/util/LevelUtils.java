package skytheory.lib.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import skytheory.lib.network.BreakEffectMessage;
import skytheory.lib.network.SkyTheoryLibNetwork;

public class LevelUtils {

	public static Set<BlockPos> getAdjacentPosIncludeDiagonal(BlockPos pos) {
		Set<BlockPos> result = new HashSet<>();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					BlockPos target = pos.offset(x, y, z);
					if (!target.equals(pos) && target.distManhattan(pos) < 3) {
						result.add(target);
					}
				}
			}
		}
		return result;
	}

	public static void harvestBlock(ServerLevel level, BlockPos pos, BlockState state) {
		removeBlock(level, pos, state);
		playSound(level, pos, state);
		gatherDropsToPosition(level, pos, state);
	}

	public static void harvestBlock(ServerLevel level, BlockPos pos, BlockState state, Player player) {
		removeBlock(level, pos, state);
		playSound(level, pos, state);
		gatherDropsToPlayer(level, pos, state, player);
	}

	public static void removeBlock(ServerLevel level, BlockPos pos, BlockState state) {
		SkyTheoryLibNetwork.sendToClient(level, pos, new BreakEffectMessage(level, pos, state));
		level.removeBlock(pos, false);
	}

	public static void playSound(ServerLevel level, BlockPos pos, BlockState state) {
		SoundType soundtype = state.getSoundType(level, pos, null);
		level.playSound(null, pos, soundtype.getBreakSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
	}

	public static void gatherDropsToPosition(ServerLevel level, BlockPos pos, BlockState state) {
		List<ItemStack> drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos));
		for (ItemStack stack : drops)  {
			popItemEntityWithModulate(level, Vec3.atCenterOf(pos), stack, true);
		}
	}

	public static void popItemEntityWithModulate(ServerLevel level, Vec3 pos, ItemStack stack, boolean setPickUpDelay) {
		double hMod = EntityType.ITEM.getHeight() / 2.0d;
		double x = pos.x() + Mth.nextDouble(level.random, -0.25d, 0.25d);
		double y = pos.y() + Mth.nextDouble(level.random, -0.25d, 0.25d) - hMod;
		double z = pos.z() + Mth.nextDouble(level.random, -0.25d, 0.25d);
		double xMod = Mth.nextDouble(level.getRandom(), -0.1d, 0.1d);
		double yMod = Mth.nextDouble(level.getRandom(), 0.0d, 0.1d);
		double zMod = Mth.nextDouble(level.getRandom(), -0.1d, 0.1d);
		ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack, xMod, yMod, zMod);
		itemEntity.setDefaultPickUpDelay();
		if (setPickUpDelay) itemEntity.setDefaultPickUpDelay();
		level.addFreshEntity(itemEntity);
	}

	public static void gatherDropsToPlayer(ServerLevel level, BlockPos pos, BlockState state, Player player) {
		gatherDropsToPlayer(level, pos, state, player, ItemStack.EMPTY);
	}
	
	public static void gatherDropsToPlayer(ServerLevel level, BlockPos pos, BlockState state, Player player, ItemStack tool) {
		List<ItemStack> drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos), player, tool);
		for (ItemStack stack : drops)  {
			if (!player.isCreative()) {
				ItemHandlerUtils.giveItemToPlayerWithDropItem(player, stack);
			}
		}
		removeBlock(level, pos, state);
	}

}
