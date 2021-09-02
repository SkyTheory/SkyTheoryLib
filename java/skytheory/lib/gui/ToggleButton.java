package skytheory.lib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public class ToggleButton extends AbstractButton {

	private boolean state;
	private boolean hold;

	public ToggleButton(GuiContainer gui, int id, int x, int y, int texX, int texY, int width, int height, ResourceLocation location) {
		super(gui, id, x, y, texX, texY, width, height, location);
		this.state = false;
	}
	public void on() {
		this.setState(true);
	}

	public void off() {
		this.setState(false);
	}

	public void toggle() {
		this.setState(!state);
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean getState() {
		return this.state;
	}

	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean result = this.getVisible() && this.isMouseOver();
		if (result) this.hold = true;
		return result;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		super.drawButton(mc, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean isMouseOver() {
		boolean result = this.hovered;
		if (!result) this.hold = false;
		return result;
	}

	@Override
	protected int getTextureU() {
		return texU;
	}

	@Override
	protected int getTextureV() {
		if (this.getState()) {
			if (!this.isMouseOver() || hold) return texV + height; else return texV;
		} else {
			if (!this.isMouseOver() || hold) return texV; else return texV + height;
		}
	}
}
