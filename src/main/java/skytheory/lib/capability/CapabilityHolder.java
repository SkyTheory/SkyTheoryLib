package skytheory.lib.capability;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface CapabilityHolder {

	List<CapabilityEntry> getCapabilityProviders();

	public static record CapabilityEntry(ResourceLocation key, ICapabilityProvider provider) {}

}
