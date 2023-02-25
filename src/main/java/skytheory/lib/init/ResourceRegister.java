package skytheory.lib.init;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import skytheory.lib.block.BlockItemSpecifier;

public class ResourceRegister {

	private static final Logger LOGGER = LogUtils.getLogger();

	public static <T> void registerAll(RegisterEvent event, Class<? super T> type, ResourceKey<? extends Registry<T>> registryKey, Class<?> holder) {
		registerAll(event, type, registryKey, ModLoadingContext.get().getActiveNamespace(), holder);
	}
	
	public static <T> void registerAll(RegisterEvent event, Class<? super T> type, ResourceKey<? extends Registry<T>> registryKey, String modid, Class<?> holder) {
		if (type == Block.class) {
			registerItemBlocks(event, modid, holder);
		}
		if (!event.getRegistryKey().equals(registryKey)) return;
		List<Pair<ResourceLocation, T>> list = new ArrayList<>();
		Arrays.stream(holder.getDeclaredFields())
		.filter(ResourceRegister::validate)
		.filter(field -> type.isAssignableFrom(field.getType()))
		.forEach(field -> list.add(getEntry(modid, field)));
		list.stream()
		.sorted(Comparator.comparing(Pair::getKey))
		.forEach(entry -> register(event, registryKey, entry));
	}

	public static void registerItemBlocks(RegisterEvent event, Class<?> holder) {
		registerItemBlocks(event, ModLoadingContext.get().getActiveNamespace(), holder);
	}

	public static void registerItemBlocks(RegisterEvent event, String modid, Class<?> holder) {
		if (!event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) return;
		List<Pair<ResourceLocation, Item>> list = new ArrayList<>();
		Arrays.stream(holder.getDeclaredFields())
		.filter(ResourceRegister::validate)
		.filter(field -> Block.class.isAssignableFrom(field.getType()))
		.map(field -> ResourceRegister.createBlockItemEntry(getEntry(modid, field)))
		.filter(Optional::isPresent)
		.map(Optional::get)
		.forEach(list::add);
		list.forEach(entry -> register(event, ForgeRegistries.Keys.ITEMS, entry));
	}
	
	public static <T> void register(RegisterEvent event, ResourceKey<? extends Registry<T>> key, Pair<ResourceLocation, T> entry) {
		event.register(key, entry.getKey(), entry::getValue);
	}

	private static boolean validate(Field field) {
		int mod = field.getModifiers();
		return Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod);
	}

	private static <T> Pair<ResourceLocation, T> getEntry(String modid, Field field) {
		try {
			@SuppressWarnings("unchecked")
			T entry = (T) field.get(field.getDeclaringClass());
			return Pair.of(new ResourceLocation(modid, field.getName().toLowerCase()), entry);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			LOGGER.error("An exception occurred during registration: " + field.getName());
			LOGGER.error("Class: " + field.getDeclaringClass().getCanonicalName());
			throw new UnsupportedOperationException(e);
		}
	}

	private static Optional<Pair<ResourceLocation, Item>> createBlockItemEntry(Pair<ResourceLocation, Block> entry) {
		if (entry.getValue() instanceof BlockItemSpecifier specifier) {
			Item item = specifier.createItem();
			if (item == Items.AIR) return Optional.empty();
			return Optional.of(Pair.of(entry.getKey(), item));
		}
		return Optional.of(Pair.of(entry.getKey(), new BlockItem(entry.getValue(), new Item.Properties())));
	}

}
