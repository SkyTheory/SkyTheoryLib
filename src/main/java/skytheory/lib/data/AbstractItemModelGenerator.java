package skytheory.lib.data;

import java.util.List;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class AbstractItemModelGenerator extends ItemModelProvider {

	public AbstractItemModelGenerator(PackOutput generator, String modid, ExistingFileHelper existingFileHelper) {
		super(generator, modid, existingFileHelper);
	}

	@Override
	public ItemModelBuilder basicItem(Item item) {
		ResourceLocation path = ForgeRegistries.ITEMS.getKey(item);
		// BlockItem
		if (item instanceof BlockItem blockItem) {
			ResourceLocation blockPath = ForgeRegistries.BLOCKS.getKey(blockItem.getBlock());
			ResourceLocation modelPath = new ResourceLocation(blockPath.getNamespace(), "block/" + blockPath.getPath());
			ModelFile parent = new UncheckedModelFile(modelPath);
			return getBuilder(path, parent);
		// SpawnEgg
		} else if (item instanceof ForgeSpawnEggItem) {
			ModelFile parent = new ModelFile.UncheckedModelFile("item/template_spawn_egg");
			return getBuilder(path, parent);
		// Item
		} else {
			ResourceLocation texturePath = new ResourceLocation(path.getNamespace(), "item/" + path.getPath());
			return basicItem(path, texturePath);
		}
	}

	public ItemModelBuilder basicItem(Item item, String category) {
		ResourceLocation path = ForgeRegistries.ITEMS.getKey(item);
		ResourceLocation texturePath = new ResourceLocation(path.getNamespace(), "item/" + category + "/" + path.getPath());
		return getBuilder(path.toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture("layer0", texturePath);
	}

	public ItemModelBuilder basicItem(ResourceLocation path, ResourceLocation texturePath) {
		return getBuilder(path.toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture("layer0", texturePath);
	}
	
	public ItemModelBuilder getBuilder(ResourceLocation path, ModelFile parent) {
		return getBuilder(path.toString()).parent(parent);
	}

	@Override
    protected final void registerModels() {
		registerItemModels();
		List<ResourceLocation> entries = this.getAllEntries();
		generatedModels.keySet().forEach(key -> entries.remove(key));
		entries.forEach(key -> this.basicItem(ForgeRegistries.ITEMS.getValue(key)));
    }
	
	protected abstract void registerItemModels(); 
	
	protected List<ResourceLocation> getAllEntries() {
		List<ResourceLocation> result = ForgeRegistries.ITEMS.getEntries().stream()
				.map(entry -> entry.getKey().location())
				.filter(location -> modid.equals(location.getNamespace()))
				.toList();
		return result;
	}
}
