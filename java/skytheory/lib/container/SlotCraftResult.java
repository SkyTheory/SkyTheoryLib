package skytheory.lib.container;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

public class SlotCraftResult extends SlotInventory {

	public final EntityPlayer player;
	public final InventoryCrafting craftmatrix;
	public final InventoryCraftResult craftresult;
	private int amountCrafted;

	public SlotCraftResult(EntityPlayer player, InventoryCrafting craftmatrix, InventoryCraftResult craftresult) {
		super(craftresult, 0);
		this.player = player;
		this.craftmatrix = craftmatrix;
		this.craftresult = craftresult;
	}

	public SlotCraftResult(EntityPlayer player, InventoryCrafting craftmatrix, InventoryCraftResult craftresult, int xPosition, int yPosition) {
		super(craftresult, 0, xPosition, yPosition);
		this.player = player;
		this.craftmatrix = craftmatrix;
		this.craftresult = craftresult;
	}

	/**
	 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
	 */
	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}

	/**
	 * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
	 * stack.
	 */
	@Override
	public ItemStack decrStackSize(int amount) {
		if (this.getHasStack()) {
			this.amountCrafted += Math.min(amount, this.getStack().getCount());
		}
		return super.decrStackSize(amount);
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
	 * internal count then calls onCrafting(item).
	 */
	@Override
	protected void onCrafting(ItemStack stack, int amount) {
		this.amountCrafted += amount;
		this.onCrafting(stack);
	}

	@Override
	protected void onSwapCraft(int p_190900_1_) {
		this.amountCrafted += p_190900_1_;
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
	 */
	@Override
	protected void onCrafting(ItemStack stack) {
		if (this.amountCrafted > 0) {
			stack.onCrafting(this.player.world, this.player, this.amountCrafted);
			net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(this.player, stack, craftmatrix);
		}
		this.amountCrafted = 0;
		IRecipe irecipe = this.craftresult.getRecipeUsed();
		if (irecipe != null && !irecipe.isDynamic()) {
			this.player.unlockRecipes(Lists.newArrayList(irecipe));
			this.craftresult.setRecipeUsed((IRecipe)null);
		}
	}

	@Override
	public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
		this.onCrafting(stack);
		net.minecraftforge.common.ForgeHooks.setCraftingPlayer(playerIn);
		NonNullList<ItemStack> remains = CraftingManager.getRemainingItems(this.craftmatrix, playerIn.world);
		net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);
		for (int i = 0; i < remains.size(); ++i) {
			ItemStack ingredient = this.craftmatrix.getStackInSlot(i);
			ItemStack remain = remains.get(i);
			if (!ingredient.isEmpty()) {
				this.craftmatrix.decrStackSize(i, 1);
				ingredient = this.craftmatrix.getStackInSlot(i);
			}
			if (!remain.isEmpty()) {
				if (ingredient.isEmpty()) {
					this.craftmatrix.setInventorySlotContents(i, remain);
				}
				else if (ItemStack.areItemsEqual(ingredient, remain) && ItemStack.areItemStackTagsEqual(ingredient, remain)) {
					remain.grow(ingredient.getCount());
					this.craftmatrix.setInventorySlotContents(i, remain);
				}
				else if (!this.player.inventory.addItemStackToInventory(remain)) {
					this.player.dropItem(remain, false);
				}
			}
		}
		return stack;
	}
}
