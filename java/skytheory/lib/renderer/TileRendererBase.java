package skytheory.lib.renderer;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public abstract class TileRendererBase<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

	@Override
	public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		ResourceLocation location = this.getTexture(tile);
		if (location != null) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(location);
		}
		this.render(tile, x, y, z, partialTicks, alpha);
	}

	public void renderByItem(ItemStack stack) {
		ResourceLocation location = this.getTexture(stack);
		if (location != null) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(location);
		}
		this.render(null, 0.0d, 0.0d, 0.0d, 0.0f, -1);
	}

	public void render(@Nullable T tile, double x, double y, double z, float partialTicks, float alpha) {
		this.preRender(tile, x, y, z, partialTicks, alpha);
		this.getModel(tile).render(tile, partialTicks);
		this.postRender(tile, x, y, z, partialTicks, alpha);
	}

	public void preRender(@Nullable T tile, double x, double y, double z, float partialTicks, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(-1.0d, -1.0d, 1.0d);
		GlStateManager.translate(-0.5d, -1.5d, 0.5d);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public void postRender(@Nullable T tile, double x, double y, double z, float partialTicks, float alpha) {
		GlStateManager.popMatrix();
	}

	public abstract ResourceLocation getTexture(T tile);
	public abstract ResourceLocation getTexture(ItemStack stack);
	public abstract ITileModel<T> getModel(@Nullable T tile);
}
