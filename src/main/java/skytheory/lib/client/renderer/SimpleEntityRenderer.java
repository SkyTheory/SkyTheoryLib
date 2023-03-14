package skytheory.lib.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class SimpleEntityRenderer<T extends Entity & EntityModelSelector<T>> extends EntityRenderer<T> {

	protected final EntityModelSet entityModelSet;
	
	protected SimpleEntityRenderer(EntityRendererProvider.Context pContext) {
		super(pContext);
		this.entityModelSet = pContext.getModelSet();
	}

	public void render(T pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
		if (pEntity == null || pEntity.isInvisible()) return;
		super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
		pPoseStack.pushPose();
		pPoseStack.scale(-1.0f, -1.0f, 1.0f);
		pPoseStack.translate(0.0f, -1.5f, 0.0f);
		EntityModel<T> entityModel = pEntity.getEntityModel(pEntity, entityModelSet.bakeLayer(pEntity.getModelLayerLocation(pEntity)));
		entityModel.renderToBuffer(pPoseStack, pBuffer.getBuffer(RenderType.entityCutout(getTextureLocation(pEntity))), pPackedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
		pPoseStack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(T pEntity) {
		return pEntity.getTextureLocation(pEntity);
	}
	
}
