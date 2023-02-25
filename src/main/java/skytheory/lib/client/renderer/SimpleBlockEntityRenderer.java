package skytheory.lib.client.renderer;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import skytheory.lib.client.model.ModelSetup;

public class SimpleBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
	
	protected final BlockEntityRenderDispatcher renderer;
	private static Map<ModelLayerLocation, Model> MODEL_CACHE = new HashMap<>();
	
	public SimpleBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		this.renderer = ctx.getBlockEntityRenderDispatcher();
	}

	@Override
	public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource,
			int pPackedLight, int pPackedOverlay) {
		if (pBlockEntity.isRemoved()) return;
		Block block = pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos()).getBlock();
		if (block instanceof ModelSelector selector) {
			Model model = getModel(selector);
			if (model instanceof ModelSetup modelSetup) modelSetup.setupAnimBlockEntity(pBlockEntity, block, pPartialTick, pPoseStack);
			VertexConsumer vertexConsumer = pBufferSource.getBuffer(model.renderType(selector.getTextureLocation()));
			pPoseStack.pushPose();
			pPoseStack.scale(-1.0f, -1.0f, 1.0f);
			pPoseStack.translate(-0.5f, -1.5f, 0.5f);
			model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);
			pPoseStack.popPose();
		}
	}
	
	protected Model getModel(ModelSelector selector) {
		ModelLayerLocation location = selector.getModelLayerLocation();
		return MODEL_CACHE.computeIfAbsent(location,
				(loc) -> selector.getModelProvider().apply(Minecraft.getInstance().getEntityModels().bakeLayer(loc)));
	}
	
}
