package skytheory.lib.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public interface EntityModelSelector<T extends Entity> {
	
	ResourceLocation getTextureLocation(T entity);
	ModelLayerLocation getModelLayerLocation(T entity);
	EntityModel<T> getEntityModel(T entity, ModelPart modelPart);

}
