package skytheory.lib.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import com.mojang.logging.LogUtils;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.registries.ForgeRegistries;
import skytheory.lib.client.renderer.ModelSelector;
import skytheory.lib.network.SkyTheoryLibNetwork;

public class SetupEvent {

	@SubscribeEvent
	public static void modConstruct(FMLConstructModEvent event) {
		SkyTheoryLibNetwork.setup();
	}

	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event) {
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
	}

	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		List<ModelSelector> selectors = new ArrayList<>();
		ForgeRegistries.ITEMS.getValues().stream()
		.filter(ModelSelector.class::isInstance)
		.map(ModelSelector.class::cast)
		.forEach(selectors::add);
		
		ForgeRegistries.BLOCKS.getValues().stream()
		.filter(ModelSelector.class::isInstance)
		.map(ModelSelector.class::cast)
		.forEach(selectors::add);
		
		selectors.stream()
		.filter(distinctByModelLayerLocation())
		.forEach(selector -> event.registerLayerDefinition(selector.getModelLayerLocation(), selector.getLayerDefinitionProvider()));
	}
	
	private static Predicate<ModelSelector> distinctByModelLayerLocation() {
		Set<ModelLayerLocation> set = new HashSet<>();
		return (selector) -> set.add(selector.getModelLayerLocation());
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		Map<BlockEntityType<?>, ModelSelector> models = new HashMap<>();
		List<Block> selectors = ForgeRegistries.BLOCKS.getValues().stream()
				.filter(ModelSelector.class::isInstance)
				.toList();
		ForgeRegistries.BLOCK_ENTITY_TYPES.getValues().stream()
		.forEach(type -> gatherModelSelectors(models, selectors, type));
		models.forEach((type, selector) -> selector.registerRenderProvider(event, type));
	}
	
	private static void gatherModelSelectors(Map<BlockEntityType<?>, ModelSelector> models, List<Block> selectors, BlockEntityType<?> type) {
		List<Block> blocks = selectors.stream()
		.filter(block -> type.isValid(block.defaultBlockState()))
		.toList();
		if (!blocks.isEmpty()) {
			ModelSelector selector = (ModelSelector) blocks.get(0);
			if (blocks.size() > 1) {
				LogUtils.getLogger().warn("BlockEntityType " + BlockEntityType.getKey(type) + " has multiple model selectors.");
				LogUtils.getLogger().warn("Only " + ForgeRegistries.BLOCKS.getKey(blocks.get(0)) + " applies.");
			}
			models.put(type, selector);
		}
	}
	
}
