package skytheory.lib.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class RenderTileEntityItemStack extends TileEntityItemStackRenderer {

	private final Item item;
	private final TileRendererBase<? extends TileEntity> render;

	public RenderTileEntityItemStack(Item item, TileRendererBase<? extends TileEntity> render) {
		this.item = item;
		this.render = render;
	}

	@Override
	public void renderByItem(ItemStack itemStackIn) {
		if (itemStackIn.getItem() == item) {
			GlStateManager.pushMatrix();
			render.renderByItem(itemStackIn);
			GlStateManager.popMatrix();
		}
	}
}
