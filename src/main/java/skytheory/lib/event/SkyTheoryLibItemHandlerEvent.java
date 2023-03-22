package skytheory.lib.event;

import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import skytheory.lib.capability.CapabilityHolder;

public class SkyTheoryLibItemHandlerEvent {

	@SubscribeEvent
	public static void attachCapabilityToBlockEntity(AttachCapabilitiesEvent<BlockEntity> event) {
		if (event.getObject() instanceof CapabilityHolder holder) {
			attachCapability(event, holder);
		}
	}

	@SubscribeEvent
	public static void attachCapabilityToEntity(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof CapabilityHolder holder) {
			attachCapability(event, holder);
		}
	}

	@SubscribeEvent
	public static void attachCapabilityToItemStack(AttachCapabilitiesEvent<ItemStack> event) {
		if (event.getObject().getItem() instanceof CapabilityHolder holder) {
			attachCapability(event, holder);
		}
	}

	public static <T extends Tag> void attachCapability(AttachCapabilitiesEvent<?> event, CapabilityHolder holder) {
		holder.getCapabilityProviders().forEach(entry -> event.addCapability(entry.key(), entry.provider()));
	}

}
