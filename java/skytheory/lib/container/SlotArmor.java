package skytheory.lib.container;

import javax.annotation.Nullable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class SlotArmor extends SlotItemHandler {

	public final EntityPlayer player;
	public final EntityEquipmentSlot armortype;

	public SlotArmor(IItemHandler handler, int index, EntityPlayer player, EntityEquipmentSlot armortype) {
		super(handler, index);
		this.player = player;
		this.armortype = armortype;
	}

	public SlotArmor(IItemHandler handler, int index, int xPosition, int yPosition, EntityPlayer player, EntityEquipmentSlot armortype) {
		super(handler, index, xPosition, yPosition);
		this.player = player;
		this.armortype = armortype;
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return stack.getItem().isValidArmor(stack, this.armortype, this.player);
	}

	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		ItemStack itemstack = this.getStack();
		if (!itemstack.isEmpty()) {
			if (EnchantmentHelper.hasBindingCurse(itemstack) && !this.player.isCreative()) return false;
		}
		return super.canTakeStack(playerIn);
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	public String getSlotTexture() {
		return ItemArmor.EMPTY_SLOT_NAMES[armortype.getIndex()];
	}
}
