package skytheory.lib.client.model;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * モデルクラスにこれを継承させておけば、BlockEntityやItemStackの状態に応じてモデルの状態を切り替えることが可能になる
 * @author SkyTheory
 *
 */
public interface ModelSetup {
	
	void setupAnimBlockEntity(BlockEntity blockEntity, Block block, float partialTick, PoseStack poseStack);
	
	void setupAnimItemStack(ItemStack stack, PoseStack poseStack);
	
}
