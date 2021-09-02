package skytheory.lib.event;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import skytheory.lib.block.IWrenchBlock;
import skytheory.lib.item.IWrench;
import skytheory.lib.util.IWrenchType;
import skytheory.lib.util.WrenchHelper;
import skytheory.lib.util.WrenchRegistry;
import skytheory.lib.util.WrenchTypes;

public class WrenchEvent {

	public static final int LIMIT = 10;

	@SubscribeEvent
	public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
		ItemStack stack = event.getItemStack();
		if (stack.getItem() instanceof IWrench) {
			EntityPlayer player = event.getEntityPlayer();
			World world = player.world;
			BlockPos pos = event.getPos();
			IBlockState state = player.world.getBlockState(pos);
			Block block = state.getBlock();
			if (block instanceof IWrenchBlock) {
				IWrenchBlock wb = ((IWrenchBlock) block);
				wb.onLeftClickWithWrench(player, world, pos, event.getHand(), event.getFace());
				event.setUseBlock(Event.Result.DENY);
				return;
			}
			if (block == Blocks.END_PORTAL_FRAME) {
				if (state.getValue(BlockEndPortalFrame.EYE)) {
					detachEye(world, pos, state, player);
				}
				if (player.isSneaking()) {
					detachPortal(world, pos);
				}
				event.setUseBlock(Event.Result.DENY);
				return;
			} else if (block == Blocks.END_PORTAL && world.provider.getDimension() != 1) {
				closePortal(world, pos, 0);
				world.playSound(player, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
				event.setUseBlock(Event.Result.DENY);
				return;
			}
		}
	}

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		ItemStack stack = event.getItemStack();
		if (stack.getItem() instanceof IWrench) {
			EntityPlayer player = event.getEntityPlayer();
			World world = event.getWorld();
			BlockPos pos = event.getPos();
			EnumHand hand = event.getHand();
			EnumFacing facing =event.getFace();
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			IWrenchType type = WrenchRegistry.getType(block);
			if (type != WrenchTypes.NONE) {
				type.interact(player, world, pos, hand, facing);
			}
		}
	}

	private static void detachEye(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		boolean close = false;
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			BlockPos adjacent = pos.offset(facing);
			if (world.getBlockState(adjacent).getBlock() == Blocks.END_PORTAL) {
				close = true;
				closePortal(world, adjacent, 0);
			}
		}
		if (close) {
			world.playSound(player, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
		}
		if (!world.isRemote) {
			world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY() + 1d, pos.getZ(), new ItemStack(Items.ENDER_EYE)));
			WrenchHelper.cycleProperty(world, pos, BlockEndPortalFrame.EYE);
		}
	}

	private static void closePortal(World world, BlockPos pos, int i) {
		if (world.isRemote) return;
		if (i > LIMIT) return;
		world.setBlockToAir(pos);
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			BlockPos adjacent = pos.offset(facing);
			if (world.getBlockState(adjacent).getBlock() == Blocks.END_PORTAL) {
				closePortal(world, adjacent, ++i);
			}
		}
	}

	private static void detachPortal(World world, BlockPos pos) {
		if (world.isRemote) return;
		world.setBlockToAir(pos);
		world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.END_PORTAL_FRAME)));
	}
}
