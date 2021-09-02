package skytheory.lib.init;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.capability.DataStorage;

/**
 * 様々なものを登録するクラス<br>
 * @author SkyTheory
 *
 */
public class ResourceRegister {

	/**
	 * ForgeRegistryを新しく作成する<br>
	 * RegistryEvent.NewRegistryの時にこれを呼ぶこと
	 * @param modid
	 * @param type
	 * @param registryname
	 */
	public static <T extends IForgeRegistryEntry<T>> void create(Class<T> type, String modid, String registryname) {
		RegistryBuilder<T> builder = new RegistryBuilder<T>();
		ResourceLocation resourcelocation = new ResourceLocation(modid, registryname);
		builder.setType(type);
		builder.setName(resourcelocation);
		builder.setDefaultKey(resourcelocation);
		builder.create();
	}

	/**
	 * アイテムやブロックなどをレジストリに登録する<br>
	 * RegistryEvent.Registerの際にこれを呼ぶこと
	 * 第四引数はnullでも構わない
	 */
	public static <T extends IForgeRegistryEntry<T>> void registerAll(IForgeRegistry<T> registry, Class<?> entries, String modid, @Nullable CreativeTabs tab){
		Class<T> type = registry.getRegistrySuperType();
		Arrays.stream(entries.getFields())
		.filter(ResourceRegister::varidateField)
		.map(field -> {
			try {
				return Pair.of(field.get(entries), field.getName());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				SkyTheoryLib.LOGGER.error(e);
				SkyTheoryLib.LOGGER.error(String.format("Registration failed: %s", field.getName()));
				return Pair.of(null, "");
			}
		})
		.filter(pair -> type.isInstance(pair.getLeft()))
		.forEachOrdered(pair -> ResourceRegister.register(registry, type.cast(pair.getLeft()), modid, pair.getRight(), tab));
	}

	/**
	 * アイテムやブロックなどをレジストリに登録する<br>
	 * RegistryEvent.Registerの際にこれを呼ぶこと
	 * @param registry
	 * @param entry
	 * @param modid
	 * @param name
	 * @param tab
	 */
	public static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, T entry, String modid, String name, @Nullable CreativeTabs tab) {
		// RegistryNameがnullの場合は設定する
		if (entry.getRegistryName() == null) {
			entry.setRegistryName(new ResourceLocation(modid, name));
		}
		// アイテムまたはブロックの場合はUnlocalizedNameも設定する
		if (entry instanceof Block) {
			Block block = Block.class.cast(entry);
			if (block.getUnlocalizedName().equals("tile.null")) {
				block.setUnlocalizedName(name);
			}
			if (tab != null) {
				block.setCreativeTab(tab);
			}
		}
		if (entry instanceof Item) {
			Item item = Item.class.cast(entry);
			if (item.getUnlocalizedName().equals("item.null")) {
				item.setUnlocalizedName(name);
			}
			if (tab != null) {
				item.setCreativeTab(tab);
			}
		}
		registry.register(entry);
	}

	/**
	 * TileEntityをまとめて登録する<br>
	 * 単一の登録は普通にGameRegistry.registerTileEntityからいけるー
	 * @param entries
	 * @param modid
	 */
	@SuppressWarnings("unchecked")
	public static void registerTiles(Class<?> entries, String modid) {
		Arrays.stream(entries.getFields())
		.filter(ResourceRegister::varidateField)
		.map(field -> {
			try {
				return Pair.of(field.get(entries), field.getName());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				SkyTheoryLib.LOGGER.error(e);
				SkyTheoryLib.LOGGER.error(String.format("Registration failed: %s", field.getName()));
				return Pair.of(null, field.getName());
			}
		})
		.filter(entry -> Class.class.isInstance(entry.getLeft()))
		.map(entry -> Pair.of((Class<?>) entry.getLeft(), entry.getRight()))
		.filter(entry -> TileEntity.class.isAssignableFrom(entry.getLeft()))
		.map(entry -> Pair.of((Class<? extends TileEntity>) entry.getLeft(), entry.getRight()))
		.forEachOrdered(entry -> GameRegistry.registerTileEntity(entry.getLeft(), new ResourceLocation(modid, entry.getRight())));
	}

	/**
	 * ItemStackやEntity、TileEntityなどに持たせるカスタムデータの登録を行う<br>
	 * ここで登録したCapabilityが使用可能になる<br>
	 *
	 * @param type
	 */
	public static <T> void registerCapability(Class<T> type) {
		CapabilityManager.INSTANCE.register(type, new DataStorage<T>(), (() -> type.newInstance()));
	}


	/**
	 * アイテムモデルをまとめて登録。
	 * ClientProxyでModelRegistryEvent時に呼ぶこと
	 * @param entries
	 */
	public static void registerItemModels(Class<?> entries) {
		Arrays.stream(entries.getFields())
		.filter(ResourceRegister::varidateField)
		.map(field -> {
			try {
				return field.get(entries);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				SkyTheoryLib.LOGGER.error(e);
				SkyTheoryLib.LOGGER.error(String.format("Registration failed: %s", field.getName()));
				return null;
			}
		})
		.filter(Item.class::isInstance)
		.map(Item.class::cast)
		.forEachOrdered(ResourceRegister::registerItemModel);
	}

	public static void registerItemModel(Item item) {
		ModelResourceLocation mrlocation = new  ModelResourceLocation(item.getRegistryName(), "inventory");
		ModelLoader.setCustomModelResourceLocation(item, 0, mrlocation);
	}

	public static boolean varidateField(Field field) {
		if (!Modifier.isStatic(field.getModifiers())) {
			SkyTheoryLib.LOGGER.error("Skipping registering entry: " + field.getName());
			SkyTheoryLib.LOGGER.error("Non-static field.");
			return false;
		}
		if (Modifier.isPrivate(field.getModifiers())) {
			SkyTheoryLib.LOGGER.error("Skipping registering entry: " + field.getName());
			SkyTheoryLib.LOGGER.error("Private field.");
			return false;
		}
		return true;
	}
}
