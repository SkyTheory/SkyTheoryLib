package skytheory.lib.client.renderer;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import skytheory.lib.client.model.ModelSetup;
import skytheory.lib.util.BlockRotation;
import skytheory.lib.util.BlockRotationProperty;

public class RotationalBlockEntityRenderer<T extends BlockEntity> extends SimpleBlockEntityRenderer<T> {

	public RotationalBlockEntityRenderer(Context ctx) {
		super(ctx);
	}

	@Override
	public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource,
			int pPackedLight, int pPackedOverlay) {
		if (pBlockEntity.isRemoved()) return;
		BlockState state = pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos());
		Optional<BlockRotationProperty> property = BlockRotationProperty.getProperty(state);
		if (property.isPresent()) {
			BlockRotation rotation = state.getValue(property.get());
			Block block = pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos()).getBlock();
			if (block instanceof ModelSelector selector) {
				Model model = getModel(selector);
				if (model instanceof ModelSetup modelSetup) modelSetup.setupAnimBlockEntity(pBlockEntity, block, pPartialTick, pPoseStack);
				VertexConsumer vertexConsumer = pBufferSource.getBuffer(model.renderType(selector.getTextureLocation()));
				pPoseStack.scale(-1.0f, -1.0f, 1.0f);
				pPoseStack.translate(-0.5f, -0.5f, 0.5f);
				pPoseStack.pushPose();
				if (rotation != null) {
					pPoseStack.mulPose(rotation.getQuaternion());
				}
				pPoseStack.translate(0.0f, -1.0f, 0.0f);
				model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);
				pPoseStack.popPose();
			}
		} else {
			LogUtils.getLogger().error("Trying to draw a BlockState that does not support Block Rotation; check the Block state.");
			super.render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
		}
	}

}
