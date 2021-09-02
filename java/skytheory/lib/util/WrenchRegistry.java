package skytheory.lib.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.GameRegistry;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.block.IWrenchBlock;
import skytheory.lib.config.Config;

public class WrenchRegistry {

	private static final Map<Block, IWrenchType> TYPE_MAP = new HashMap<>();
	private static final Map<Class<?>, IWrenchType> TYPES = new HashMap<>();

	public static void init() {
		registerBlocks();
		registerTypes();
	}

	public static void postInit() {
		GameRegistry.findRegistry(Block.class).forEach(block -> {
			initializeFromType(block);
			initializeFromProperty(block);
		});
	}

	/**
	 * ブロックに対してWrenchTypeを登録する<br>
	 * initのタイミングで呼ぶこと
	 * @param block
	 * @param type
	 */
	public static void register(Block block, IWrenchType type) {
		if (!TYPE_MAP.containsKey(block)) {
			TYPE_MAP.put(block, type);
			if (Config.log_wrench) {
				SkyTheoryLib.LOGGER.info("Register wrench type: <" + block.getRegistryName() + "> : " + type.getName());
			}
		} else {
			// 覚書：TYPESの登録が重複していると呼ばれる
			// 単一のブロックに対して優先するべきものをコーディングしていくことで解消できる
			if (TYPE_MAP.get(block) != WrenchTypes.NONE) {
				IWrenchType old = TYPE_MAP.get(block);
				if (old != type) {
					TYPE_MAP.put(block, type);
					if (Config.log_wrench) {
						SkyTheoryLib.LOGGER.warn("Override wrench type: <" + block.getRegistryName() + "> : " + old.getName() + " -> " + type.getName());
					}
				}
			}
		}
	}

	/**
	 * 特定のクラスを継承したブロックをまとめて登録するならこちら<br>
	 * initのタイミングでここから登録すること
	 * @param clazz
	 * @param type
	 */
	public static void registerType(Class<? extends Block> clazz, IWrenchType type) {
		TYPES.put(clazz, type);
	}

	public static IWrenchType getType(Block block) {
		return TYPE_MAP.getOrDefault(block, WrenchTypes.NONE);
	}

	private static void registerBlocks() {
		register(Blocks.END_PORTAL_FRAME, WrenchTypes.END_PORTAL);
		register(Blocks.STANDING_BANNER, WrenchTypes.BANNER_STANDING);
		register(Blocks.STANDING_SIGN, WrenchTypes.SIGN_STANDING);
		register(Blocks.WALL_BANNER, WrenchTypes.BANNER_WALL);
		register(Blocks.WALL_SIGN, WrenchTypes.SIGN_WALL);
	}

	private static void registerTypes() {
		registerType(BlockPistonExtension.class, WrenchTypes.NONE);

		registerType(BlockBed.class, WrenchTypes.BED);
		registerType(BlockButton.class, WrenchTypes.WALL);
		registerType(BlockChest.class, WrenchTypes.CHEST);
		registerType(BlockDoor.class, WrenchTypes.DOOR);
		registerType(BlockRotatedPillar.class, WrenchTypes.AXIS);
		registerType(BlockSlab.class, WrenchTypes.SLAB);
		registerType(BlockStairs.class, WrenchTypes.STAIRS);
		registerType(BlockLadder.class, WrenchTypes.WALL);
		registerType(BlockTorch.class, WrenchTypes.WALL);
		registerType(BlockTrapDoor.class, WrenchTypes.TRAP_DOOR);

		TYPES.put(IWrenchBlock.class, WrenchTypes.WRENCH_BLOCK);
	}

	private static void initializeFromType(Block block) {
		if (TYPE_MAP.containsKey(block)) return;
		TYPES.forEach((clazz, type) -> {
			if (clazz.isAssignableFrom(block.getClass())) {
				register(block, type);
			}
		});
	}

	private static void initializeFromProperty(Block block) {
		if (TYPE_MAP.containsKey(block)) return;
		Collection<IProperty<?>> properties = block.getBlockState().getProperties();
		IProperty<EnumFacing> property1 = WrenchHelper.getFacingContainer(properties);
		if (property1 != null) {
			if (property1.getAllowedValues().size() == 6) {
				register(block, WrenchTypes.ALL_FACINGS);
				return;
			} else if (property1.getAllowedValues().size() == 5) {
				register(block, WrenchTypes.QUINTET);
				return;
			} else if (property1.getAllowedValues().containsAll(Arrays.asList(EnumFacing.HORIZONTALS))) {
				register(block, WrenchTypes.HORIZONTAL);
				return;
			} else if (property1.getAllowedValues().size() > 1) {
				register(block, WrenchTypes.CYCLE);
				return;
			}
		}

		IProperty<?> property2 = WrenchHelper.getAxisContainer(properties);
		if (property2 != null) {
			if (property2.getAllowedValues().size() > 1) {
				register(block, WrenchTypes.AXIS);
			}
		}
	}

}
