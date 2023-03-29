package skytheory.lib.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.block.model.BlockModel.GuiLight;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class AbstractBlockModelGenerator extends BlockStateProvider {

	protected final String modid;
	protected final ExistingFileHelper fh;
	
	public AbstractBlockModelGenerator(PackOutput gen, String modid, ExistingFileHelper exFileHelper) {
		super(gen, modid, exFileHelper);
		this.modid = modid;
		this.fh = exFileHelper;
	}

	/**
	 * 指定されたモデルを用いて単一BlockStateのブロックを登録する
	 */
	public void registerBlockModel(Block block) {
		ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
		registerBlockModel(block, new ResourceLocation(location.getNamespace(), "block/" + location.getPath()));
	}
	
	public void registerBlockModel(Block block, ResourceLocation location) {
		ConfiguredModel configured = new ConfiguredModel(new ExistingModelFile(location, fh));
		this.getVariantBuilder(block).partialState().setModels(configured);
	}
	
	/**
	 * 指定されたモデルを継承し、テクスチャの指定などを行うためのBlockModelBuilderを返す
	 */
	public BlockModelBuilder extendBlockModel(Block block, ResourceLocation location) {
		ModelFile parent = new ExistingModelFile(location, fh);
		BlockModelBuilder model = this.models().getBuilder(this.getRegistryKey(block).getPath()).parent(parent);
		return model;
	}
	
	public void layeredOre(Block block, Block base, ResourceLocation blockLocation) {
		this.layeredOre(block, this.getPrefixedLocation(base), blockLocation);
	}
	
	public void layeredOre(Block block, ResourceLocation baseLocation, ResourceLocation blockLocation) {
		BlockModelBuilder model = this.extendBlockModel(block, new ResourceLocation("stlib", "block/layered_block"))
		.texture("base", baseLocation)
		.texture("mineral", blockLocation)
		.renderType(new ResourceLocation("translucent"));
		ConfiguredModel configured = new ConfiguredModel(model);
		this.getVariantBuilder(block).partialState().setModels(configured);
	}
	
	/**
	 * BlockEntityのために、何も描画しないモデルを作成する
	 */
	public void blockEntity(Block block, ResourceLocation particle) {
		ModelFile.UncheckedModelFile parent = new ModelFile.UncheckedModelFile("builtin/entity");
		BlockModelBuilder model = this.models().getBuilder(this.getRegistryKey(block).getPath())
				.parent(parent)
				.ao(false)
				.texture("particle", particle)
				.guiLight(GuiLight.SIDE)
				.transforms()
				.transform(TransformType.GUI)
				.rotation(30.0f, 225.0f, 0.0f)
				.translation(0.0f, 0.0f, 0.0f)
				.scale(0.625f, 0.625f, 0.625f)
				.end()
				.transform(TransformType.GROUND)
				.rotation(0.0f, 0.0f, 0.0f)
				.translation(0.0f, 0.0f, 0.0f)
				.scale(0.25f, 0.25f, 0.25f)
				.end()
				.transform(TransformType.FIXED)
				.rotation(0.0f, 0.0f, 0.0f)
				.translation(0.0f, 0.0f, 0.0f)
				.scale(0.5f, 0.5f, 0.5f)
				.end()
				.transform(TransformType.THIRD_PERSON_RIGHT_HAND)
				.rotation(75.0f, 45.0f, 0.0f)
				.translation(0.0f, 2.5f, 0.0f)
				.scale(0.375f, 0.375f, 0.375f)
				.end()
				.transform(TransformType.THIRD_PERSON_LEFT_HAND)
				.rotation(75.0f, 225.0f, 0.0f)
				.translation(0.0f, 2.5f, 0.0f)
				.scale(0.375f, 0.375f, 0.375f)
				.end()
				.transform(TransformType.FIRST_PERSON_RIGHT_HAND)
				.rotation(0.0f, 45.0f, 0.0f)
				.translation(0.0f, 2.5f, 0.0f)
				.scale(0.4f, 0.4f, 0.4f)
				.end()
				.transform(TransformType.FIRST_PERSON_LEFT_HAND)
				.rotation(0.0f, 45.0f, 0.0f)
				.translation(0.0f, 2.5f, 0.0f)
				.scale(0.4f, 0.4f, 0.4f)
				.end()
				.end();
		ConfiguredModel configured = new ConfiguredModel(model);
		this.getVariantBuilder(block).partialState().setModels(configured);
	}

	public ResourceLocation getPrefixedLocation(Block block) {
		ResourceLocation location = getRegistryKey(block);
		return new ResourceLocation(location.getNamespace(), "block/" + location.getPath());
	}
	
	public String getPath(Block block) {
		return getRegistryKey(block).getPath();
	}

	public ResourceLocation getRegistryKey(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block);
	}

	/**
	 * 基本的なブロックは自動で登録するので、それ以外の特殊なモデルを用いるブロックを登録する
	 */
	protected abstract void registerModels();
	
	@Override
	protected void registerStatesAndModels() {
		registerModels();
		List<Block> entries = this.getAllEntries();
		registeredBlocks.keySet().forEach(entries::remove);
		entries.forEach(this::simpleBlock);
	}
	
	protected List<Block> getAllEntries() {
		List<Block> result = ForgeRegistries.BLOCKS.getEntries().stream()
				.filter(entry -> modid.equals(entry.getKey().location().getNamespace()))
				.map(entry -> entry.getValue())
				.toList();
		return new ArrayList<>(result);
	}
}
