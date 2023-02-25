package skytheory.lib.data.loot;

import java.util.stream.Stream;

import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class AbstractEntityLoot extends EntityLootSubProvider {

	private final String modid;
	
	public AbstractEntityLoot(String modid) {
		super(FeatureFlags.REGISTRY.allFlags());
		this.modid = modid;
	}
	
	/**
	 * 内容の検証に使うEntityのリストを取得する
	 */
	@Override
	protected Stream<EntityType<?>> getKnownEntityTypes() {
		return ForgeRegistries.ENTITY_TYPES.getEntries().stream()
				.filter(entry -> modid.equals(entry.getKey().location().getNamespace()))
				.map(entry -> entry.getValue());
	}
}
