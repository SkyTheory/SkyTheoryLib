package skytheory.lib.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public abstract class GuiBase<T extends Container> extends GuiContainer {

	public static final int TEXTURE_SIZE = 16;
	public final T container;
	private final ResourceLocation location;

	public GuiBase(T container, int width, int height, ResourceLocation location) {
		super(container);
		this.container = container;
		this.xSize = width;
		this.ySize = height;
		this.location = location;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(location);
		this.drawTexturedModalRect(this.getGuiLeft(), this.getGuiTop(), 0, 0, this.getXSize(), this.getYSize());
	}

	/**
	 *  タンクなどのために液体をGUI上で描画する<br>
	 * @param fluidStack
	 * @param capacity
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void drawFluidContents(FluidStack stack, int capacity, int x, int y, int width, int height) {
		if (stack == null) return;
		Fluid fluid = stack.getFluid();
		RenderUtil.setFluidColor(fluid);
		ResourceLocation location = fluid.getStill();
		TextureAtlasSprite fluidSprite = mc.getTextureMapBlocks().getAtlasSprite(location.toString());
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		int minX = x + this.guiLeft;
		int maxX = x + width + this.guiLeft;
		int minY = y + this.guiTop;
		int maxY = y + height + this.guiTop;
		double minU = fluidSprite.getMinU();
		double maxU = fluidSprite.getMaxU();
		double minV = fluidSprite.getMinV();
		double maxV = fluidSprite.getMaxV();
		double z = (double) this.zLevel;
		// 左から右へ描画する
		for (int drawX = minX; drawX < maxX; drawX += TEXTURE_SIZE) {
			int drawMinX = drawX;
			int drawMaxX = drawX + TEXTURE_SIZE;
			double drawMaxU = maxU;
			if (drawMaxX > maxX) {
				double spriteSize = maxU - minU;
				double delta = drawMaxX - maxX;
				double deltaRate = delta / TEXTURE_SIZE;
				drawMaxX = maxX;
				drawMaxU -= spriteSize * deltaRate;
			}
			// 下から上へ描画する
			for (int drawY = maxY; drawY > minY; drawY -= TEXTURE_SIZE) {
				int drawMinY = drawY - TEXTURE_SIZE;
				int drawMaxY = drawY;
				double drawMinV = minV;
				if (drawMinY < minY) {
					double spriteSize = maxV - minV;
					double delta = minY - drawMinY;
					double deltaRate = delta / TEXTURE_SIZE;
					drawMinY = minY;
					drawMinV += spriteSize * deltaRate;
				}
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				bufferbuilder.pos(drawMinX, drawMaxY, z).tex(minU, maxV).endVertex();
				bufferbuilder.pos(drawMaxX, drawMaxY, z).tex(drawMaxU, maxV).endVertex();
				bufferbuilder.pos(drawMaxX, drawMinY, z).tex(drawMaxU, drawMinV).endVertex();
				bufferbuilder.pos(drawMinX, drawMinY, z).tex(minU, drawMinV).endVertex();
				tessellator.draw();
			}
		}
		RenderUtil.resetColor();
	}
}
