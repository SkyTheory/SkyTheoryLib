package skytheory.lib.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public class HoverButton extends AbstractButton {

	public HoverButton(GuiContainer gui, int id, int x, int y, int texU, int texV, int width, int height, ResourceLocation location) {
		super(gui, id, x, y, texU, texV, width, height, location);
	}

	@Override
	protected int getTextureU() {
		return texU;
	}

	@Override
	protected int getTextureV() {
		if (!this.isEnabled()) return texV + height * 2;
		if (this.isMouseOver()) return texV + height;
		return texV;
	}
}
