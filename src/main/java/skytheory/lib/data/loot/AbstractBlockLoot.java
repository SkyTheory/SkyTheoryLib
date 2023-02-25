package skytheory.lib.data.loot;

import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class AbstractBlockLoot extends BlockLootSubProvider {

	protected final String modid;
	
	protected AbstractBlockLoot(Set<Block> pExplosionResistant, String modid) {
		super(pExplosionResistant.stream().map(ItemLike::asItem).collect(Collectors.toSet()), FeatureFlags.REGISTRY.allFlags());
		this.modid = modid;
	}

	/**
	 * 内容の検証に使うBlockのリストを取得する
	 */
	protected Iterable<Block> getKnownBlocks() {
		return ForgeRegistries.BLOCKS.getEntries().stream()
				.filter(entry -> modid.equals(entry.getKey().location().getNamespace()))
				.map(entry -> entry.getValue())
				.toList();
	}
	
}
