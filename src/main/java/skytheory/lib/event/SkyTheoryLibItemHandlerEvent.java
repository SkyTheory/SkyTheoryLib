package skytheory.lib.event;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;
import skytheory.lib.SkyTheoryLib;
import skytheory.lib.capability.DataProvider;
import skytheory.lib.capability.MultiDataSerializer;
import skytheory.lib.capability.itemhandler.ItemHandler;
import skytheory.lib.capability.itemhandler.ItemHandlerEntity;
import skytheory.lib.capability.itemhandler.ItemHandlerItem;
import skytheory.lib.capability.itemhandler.ItemHandlerListener;
import skytheory.lib.capability.itemhandler.ItemHandlerListenerItem;
import skytheory.lib.util.CapabilityUtils;

public class SkyTheoryLibItemHandlerEvent {


	public static final ResourceLocation ITEM_HANDLER_KEY = new ResourceLocation(SkyTheoryLib.MODID, "item");

	@SubscribeEvent
	public static void attachCapabilityToBlockEntity(AttachCapabilitiesEvent<BlockEntity> event) {
		if (event.getObject() instanceof ItemHandlerEntity holder) {
			attachItemHandlerCapabilityToEntity(event, holder);
		}
	}

	@SubscribeEvent
	public static void attachCapabilityToEntity(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof ItemHandlerEntity holder) {
			attachItemHandlerCapabilityToEntity(event, holder);
		}
	}

	@SubscribeEvent
	public static void attachCapabilityToItemStack(AttachCapabilitiesEvent<ItemStack> event) {
		if (event.getObject().getItem() instanceof ItemHandlerItem holder) {
			attachItemHandlerCapabilityToItemStack(event, holder);
		}
	}

	public static void attachItemHandlerCapabilityToEntity(AttachCapabilitiesEvent<?> event, ItemHandlerEntity holder) {
		List<IItemHandler> handlers = holder.createAllHandlers();
		registerItemHandlerListener(handlers, event.getObject());
		INBTSerializable<ListTag> serializer = new MultiDataSerializer(handlers);
		DataProvider<ListTag> provider = DataProvider.createSided(ForgeCapabilities.ITEM_HANDLER, holder::getItemHandler, serializer);
		event.addCapability(ITEM_HANDLER_KEY, provider);
	}

	public static void attachItemHandlerCapabilityToItemStack(AttachCapabilitiesEvent<ItemStack> event, ItemHandlerItem holder) {
		Map<Direction, IItemHandler> handlersMap = ImmutableMap.copyOf(holder.createAllHandlers());
		List<IItemHandler> handlersList = Stream.of(CapabilityUtils.DIRECTIONS)
				.map(handlersMap::get)
				.filter(Objects::nonNull)
				.toList();
		registerItemHandlerListenerItem(handlersList, event.getObject());
		INBTSerializable<ListTag> serializer = new MultiDataSerializer(handlersList);
		DataProvider<ListTag> provider = DataProvider.createSided(ForgeCapabilities.ITEM_HANDLER, handlersMap::get, serializer);
		event.addCapability(ITEM_HANDLER_KEY, provider);
	}

	private static void registerItemHandlerListener(List<IItemHandler> handlers, Object obj) {
		if (obj instanceof ItemHandlerListener listener) {
			handlers.stream()
			.filter(ItemHandler.class::isInstance)
			.map(ItemHandler.class::cast)
			.forEach(handler -> handler.addListener(listener));
		}
	}

	private static void registerItemHandlerListenerItem(List<IItemHandler> handlers, ItemStack stack) {
		if (stack.getItem() instanceof ItemHandlerListenerItem listener) {
			handlers.stream()
			.filter(ItemHandler.class::isInstance)
			.map(ItemHandler.class::cast)
			.forEach(handler -> handler.addListener((h, s) -> listener.onItemHandlerChanged(stack, h, s)));
		}
	}

}
