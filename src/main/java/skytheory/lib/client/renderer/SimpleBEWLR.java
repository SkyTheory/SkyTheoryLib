package skytheory.lib.client.renderer;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import skytheory.lib.client.model.ModelSetup;

public class SimpleBEWLR extends BlockEntityWithoutLevelRenderer {

	public static final SimpleBEWLR INSTANCE = new SimpleBEWLR();
	private static Map<ModelLayerLocation, Model> MODEL_CACHE = new HashMap<>();
	
	protected SimpleBEWLR() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType type, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
		ModelSelector selector = null;
		if (stack.getItem() instanceof ModelSelector itemModelSelector) {
			selector = itemModelSelector;
		} else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ModelSelector blockModelSelector) {
			selector = blockModelSelector;
		}
		if (selector != null) {
			Model model = getModel(selector);
			if (model instanceof ModelSetup modelSetup) modelSetup.setupAnimItemStack(stack, pPoseStack);
			VertexConsumer vertexConsumer = pBufferSource.getBuffer(model.renderType(selector.getTextureLocation()));
			pPoseStack.pushPose();
			pPoseStack.scale(-1.0f, -1.0f, 1.0f);
			pPoseStack.translate(-0.5f, -1.5f, 0.5f);
			model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);
			pPoseStack.popPose();
		} else {
			// 一応フェイルファスト
			throw new RuntimeException("Failed to get ModelSelector.");
		}
	}
	
	protected Model getModel(ModelSelector selector) {
		ModelLayerLocation location = selector.getModelLayerLocation();
		return MODEL_CACHE.computeIfAbsent(location,
				(loc) -> selector.getModelProvider().apply(Minecraft.getInstance().getEntityModels().bakeLayer(loc)));
	}
	
}
