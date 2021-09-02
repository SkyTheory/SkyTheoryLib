package skytheory.lib.container;

import javax.annotation.Nullable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class SlotOffHand extends SlotItemHandler {

	public SlotOffHand(IItemHandler handler, int index) {
		super(handler, index);
	}

	public SlotOffHand(IItemHandler handler, int index, int xPosition, int yPosition) {
		super(handler, index, xPosition, yPosition);
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	public String getSlotTexture()
	{
		return "minecraft:items/empty_armor_slot_shield";
	}
}
