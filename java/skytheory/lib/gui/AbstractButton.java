package skytheory.lib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractButton extends GuiButton {

	public final ResourceLocation location;
	public final int texU;
	public final int texV;

	public AbstractButton(GuiContainer gui, int id, int x, int y, int texU, int texV, int width, int height, ResourceLocation location) {
		super(id, gui.getGuiLeft() + x, gui.getGuiTop() + y, width, height, "");
		this.location = location;
		this.enabled = true;
		this.texU = texU;
		this.texV = texV;
	}

	public void enable() {
		setEnabled(true);
	}

	public void disable() {
		setEnabled(false);
	}

	public void setEnabled(boolean state) {
		this.enabled = state;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void show() {
		setVisible(true);
	}

	public void hide() {
		setVisible(false);
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean getVisible() {
		return visible;
	}

	@Override
	public void playPressSound(SoundHandler soundHandlerIn) {
		if (shouldPlaySound()) {
			super.playPressSound(soundHandlerIn);
		}
	}

	public boolean shouldPlaySound() {
		return visible && enabled;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			this.hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
			int u = this.getTextureU();
			int v = this.getTextureV();
			mc.getTextureManager().bindTexture(location);
			GlStateManager.disableDepth();
			this.drawTexturedModalRect(x, y, u, v, width, height);
			GlStateManager.enableDepth();
		}
	}

	protected abstract int getTextureU();
	protected abstract int getTextureV();
}
