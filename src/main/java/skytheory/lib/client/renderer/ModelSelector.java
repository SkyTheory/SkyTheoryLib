package skytheory.lib.client.renderer;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;

public interface ModelSelector {

	/**
	 * 通常はモデルのコンストラクタをメソッド参照のかたちで渡せば良い
	 * @return
	 */
	Function<ModelPart, Model> getModelProvider();
	
	/**
	 * 通常はモデルクラスにあるcreateBodyLayerをメソッド参照のかたちで渡せば良い
	 * @return
	 */
	Supplier<LayerDefinition> getLayerDefinitionProvider();
	
	/**
	 * テクスチャのある場所を指定する
	 */
	ResourceLocation getTextureLocation();
	
	/**
	 * モデルのある場所を指定する
	 */
	default ModelLayerLocation getModelLayerLocation() {
		return new ModelLayerLocation(getTextureLocation(), "main");
	}
	
	default void registerRenderProvider(EntityRenderersEvent.RegisterRenderers event, BlockEntityType<?> type) {
		event.registerBlockEntityRenderer(type, SimpleBlockEntityRenderer::new);
	}
	
}
