package skytheory.lib.init.entity;

import java.util.function.Supplier;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class EntityRenderFactory <T extends Entity> implements IRenderFactory<T> {

	private final Supplier<Render<T>> supplier;

	public EntityRenderFactory(Class<? extends Render<T>> type) {
		this.supplier = (() -> {
			try {
				return type.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("Render instanctiation failed.");
			}
		});
	}

	@Override
	public Render<T> createRenderFor(RenderManager manager) {
		return this.supplier.get();
	}

}
