package skytheory.lib.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.Fluid;

public class RenderUtil {


	/**
	 * プログレスバーやタンクの液体量などの描画量を返す
	 * @param amount
	 * @param capacity
	 * @param maxLength
	 * @return Length for render
	 */
	public static int calcRenderLength(int amount, int capacity, int maxLength) {
		float ratio = (float) amount / capacity;
		return MathHelper.ceil((float) maxLength * ratio);
	}

	/**
	 * Fluidの色をGlStateManagerに登録し、ブレンドを有効化する
	 * @param fluid
	 */
	public static void setFluidColor(Fluid fluid) {
		int color = fluid.getColor();
		float r =  ((color >> 16) & 0xFF) / 255f;
		float g = ((color >> 8) & 0xFF) / 255f;
		float b = ((color >> 0) & 0xFF) / 255f;
		float a = ((color >> 24) & 0xFF) / 255f;
		GlStateManager.color(r, g, b, a);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	/**
	 * 色を標準に戻し、ブレンドを無効化する
	 */
	public static void resetColor() {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.disableBlend();
	}

}
