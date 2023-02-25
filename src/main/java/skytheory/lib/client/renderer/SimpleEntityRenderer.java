package skytheory.lib.client.renderer;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class SimpleEntityRenderer<T extends Entity> extends EntityRenderer<T> {

	protected SimpleEntityRenderer(EntityRendererProvider.Context pContext) {
		super(pContext);
	}

	public void render(T pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
		if (pEntity == null || pEntity.isInvisible()) return;
		super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
		pPoseStack.pushPose();
		pPoseStack.scale(-1.0f, -1.0f, 1.0f);
		pPoseStack.translate(0.0f, -1.5f, 0.0f);
		getModel(pEntity).renderToBuffer(pPoseStack, pBuffer.getBuffer(RenderType.entityCutout(getTextureLocation(pEntity))), pPackedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
		pPoseStack.popPose();
	}

	protected abstract EntityModel<T> getModel(T entity);
	
	public ModelLayerLocation getModelLayerLocation(T entity) {
		return new ModelLayerLocation(getTextureLocation(entity), "main");
	}
	
	// TODO createBodyLayerの登録
	public static <T extends Entity> EntityRendererProvider<T> create(ModelLayerLocation location, Function<ModelPart, EntityModel<T>> modelProvider, ResourceLocation texture) {
		return (ctx) -> new SimpleEntityRenderer<T>(ctx) {

			ModelPart part = Minecraft.getInstance().getEntityModels().bakeLayer(location);
			EntityModel<T> model = modelProvider.apply(part);
			
			@Override
			protected EntityModel<T> getModel(T pEntity) {
				return model;
			}

			@Override
			public ResourceLocation getTextureLocation(T pEntity) {
				return texture;
			}
			
		};
	}
	
}
