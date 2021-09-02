package skytheory.lib.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

/**
 * NBTから色情報を読み取ってカラーとして返すIItemColorを継承したインターフェース<br>
 * ItemStackのNBTにSimpleColorsというキーでint型のリストを書き込むこと
 * @author SkyTheory
 *
 */
public interface SimpleNBTColor extends IItemColor {

	public static final String COLOR_KEY = "SimpleColors";

	@Override
	public default int colorMultiplier(ItemStack stack, int tintIndex) {
		if(stack.hasTagCompound()) {
			NBTTagCompound compound = stack.getTagCompound();
			if(compound.hasKey(COLOR_KEY, Constants.NBT.TAG_LIST)) {
				NBTTagList colors = compound.getTagList(COLOR_KEY, Constants.NBT.TAG_INT);
				if(tintIndex < colors.tagCount()) {
					return colors.getIntAt(tintIndex);
				}
			}
		}
		return 0xffffff;
	}

}
