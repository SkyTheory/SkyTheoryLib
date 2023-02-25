package skytheory.lib.init;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;

public class ResourceRegistry<T> {

	private final String modid;
	private final DeferredRegister<T> registry;
	private final List<T> entries = new ArrayList<>();
	private final Map<ResourceLocation, T> entriesMap = new HashMap<>();

	public static <T> ResourceRegistry<T> create(ResourceKey<? extends Registry<T>> key) {
		return create(key, ModLoadingContext.get().getActiveNamespace());
	}

	public static <T> ResourceRegistry<T> create(ResourceKey<? extends Registry<T>> key, String modid) {
		return new ResourceRegistry<>(key, modid);
	}

	private ResourceRegistry(ResourceKey<? extends Registry<T>> key, String modid) {
		this.modid = modid;
		this.registry = DeferredRegister.create(key, modid);
	}

	public void newRegistry(RegistryBuilder<T> builder) {
		this.registry.makeRegistry(() -> builder);
	}
	
	public T register(String name, T entry) {
		registry.register(name, Suppliers.ofInstance(entry));
		entries.add(entry);
		entriesMap.put(new ResourceLocation(modid, name), entry);
		return entry;
	}

	/**
	 * このレジストリを介して登録された値のリストを取得する
	 * @return
	 */
	public List<T> getValues() {
		return ImmutableList.copyOf(entries);
	}

	public Map<ResourceLocation, T> getEntries() {
		return ImmutableMap.copyOf(entriesMap);
	}
	
	/**
	 * IForgeRegistryに登録された全ての値を取得する
	 * @return
	 */
	public Collection<T> getAllValues() {
		return RegistryManager.ACTIVE.getRegistry(registry.getRegistryKey()).getValues();
	}

	public Map<ResourceLocation, T> getAllEntries() {
		Map<ResourceLocation, T> entries = new HashMap<>();
		RegistryManager.ACTIVE.getRegistry(registry.getRegistryKey()).getEntries().stream()
		.forEach(entry -> entries.put(entry.getKey().location(), entry.getValue()));
		return ImmutableMap.copyOf(entries);
	}

}
