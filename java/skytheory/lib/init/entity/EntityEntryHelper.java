package skytheory.lib.init.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

public class EntityEntryHelper {

	private static int ID;

	public static EntityEntry createEntityEntry(String modid, Class<? extends Entity> clazz, String name) {
		return getEntryBuilder(modid, clazz, name).build();
	}

	public static EntityEntry createEntityEntry(String modid, Class<? extends Entity> clazz, String name, int color1, int color2) {
		return getEntryBuilder(modid, clazz, name).egg(color1, color2).build();
	}

	private static <T extends Entity> EntityEntryBuilder<T> getEntryBuilder(String modid, Class<T> clazz, String name) {
		EntityEntryBuilder<T> entry = EntityEntryBuilder.create();
		entry.entity(clazz);
		entry.id(new ResourceLocation(modid, name), ID++);
		entry.name(modid + "." + name);
		entry.tracker(64, 3, false);
		return entry;
	}
}
