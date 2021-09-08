package skytheory.lib.util;

import java.util.Collection;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WrenchHelper {

	public static IProperty<EnumFacing> getFacingContainer(IBlockState state) {
		return getFacingContainer(state.getPropertyKeys());
	}

	public static IProperty<?> getAxisContainer(IBlockState state) {
		return getAxisContainer(state.getPropertyKeys());
	}

	public static IProperty<EnumFacing> getFacingContainer(Collection<IProperty<?>> properties){
		IProperty<EnumFacing> facings = getStateContainer("facing", properties, EnumFacing.class);
		if (facings != null) {
			return facings;
		}
		return getStateContainer("rotation", properties, EnumFacing.class);
	}

	public static IProperty<?> getAxisContainer(Collection<IProperty<?>> properties) {
		return getStateContainer("axis", properties);
	}

	public static IProperty<?> getStateContainer(String key, Collection<IProperty<?>> properties) {
		return  properties.stream().filter(property -> property.getName().equals(key)).findFirst().orElse(null);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Comparable<T>> IProperty<T> getStateContainer(String key, Collection<IProperty<?>> properties, Class<T> type) {
		IProperty<?> property = getStateContainer(key, properties);
		if (property != null && property.getValueClass() == type) {
			return (IProperty<T>) property;
		}
		return null;
	}

	public static void turnFront(EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		IBlockState state = world.getBlockState(pos);
		IProperty<EnumFacing> property = getFacingContainer(state);
		EnumFacing current = state.getValue(property);
		EnumFacing next = side;
		if (current == next) next = next.getOpposite();
		if (property.getAllowedValues().contains(next)) {
			setProperty(world, pos, state, property, next);
		} else if (property.getAllowedValues().contains(next.getOpposite())) {
			setProperty(world, pos, state, property, next.getOpposite());
		}
	}

	public static void cycleProperty(World world, BlockPos pos, IProperty<?> property) {
		IBlockState state = world.getBlockState(pos);
		if (world.isRemote) return;
		if (Objects.isNull(property)) return;
		world.setBlockState(pos, state.cycleProperty(property));
	}

	public static <T extends Comparable<T>> void setProperty(World world, BlockPos pos, IBlockState state, IProperty<T> property, T value){
		if (world.isRemote) return;
		if (state.getProperties().containsKey(property) && property.getAllowedValues().contains(value)) {
			world.setBlockState(pos, state.withProperty(property, value));
		}
	}

	public static void invertFacing(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		IProperty<EnumFacing> property = getFacingContainer(state);
		setProperty(world, pos, state, property, state.getValue(property).getOpposite());
	}

	public static void rotateFacing(World world, BlockPos pos, EnumFacing axis) {
		IBlockState state = world.getBlockState(pos);
		IProperty<EnumFacing> property = getFacingContainer(state);
		setProperty(world, pos, state, property, FacingHelper.rotateOrInvert(state.getValue(property), axis));
	}

	public static void rotateNextSolid(World world, BlockPos pos, EnumFacing axis) {
		IBlockState state = world.getBlockState(pos);
		IProperty<EnumFacing> property = getFacingContainer(state);
		Collection<EnumFacing> allowed = property.getAllowedValues();
		EnumFacing current = state.getValue(property);
		EnumFacing next = current;
		if (Objects.isNull(current)) return;
		do {
			next = FacingHelper.rotateOrInvert(next, axis);
		} while (next != current && (!isSideSolid(world, pos, state, next.getOpposite()) || !allowed.contains(next)));
		if (next != current) {
			setProperty(world, pos, state, property, next);
		}
	}

	public static void invertSolid(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		IProperty<EnumFacing> property = getFacingContainer(state);
		EnumFacing facing = state.getValue(property);
		Collection<EnumFacing> allowed = property.getAllowedValues();
		if (isSideSolid(world, pos, state, facing) && allowed.contains(facing.getOpposite())) {
			invertFacing(world, pos);
		}
	}

	public static boolean isSideSolid(World world, BlockPos pos, IBlockState state, EnumFacing facing) {
		return world.getBlockState(pos.offset(facing)).getMaterial().isSolid();
	}


	public static void changeBannerStanding(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		BlockPos target = pos.up();
		int rotation = state.getValue(BlockBanner.ROTATION);
		EnumFacing facing;
		if (rotation == 0) {
			facing = EnumFacing.SOUTH;
		} else if (rotation == 4) {
			facing = EnumFacing.WEST;
		} else if (rotation == 8) {
			facing = EnumFacing.NORTH;
		} else if (rotation == 12) {
			facing = EnumFacing.EAST;
		} else {
			return;
		}
		if (!Blocks.STANDING_BANNER.canPlaceBlockAt(world, target)) return;
		if (!world.getBlockState(target).getMaterial().isReplaceable()) return;
		if (!isSideSolid(world, target, state, facing.getOpposite())) return;
		TileEntity tile = world.getTileEntity(target);
		if (!(tile instanceof TileEntityBanner)) return;
		ItemStack stack = ((TileEntityBanner) tile).getItem();
		world.setBlockToAir(pos);
		world.setBlockState(target, Blocks.WALL_BANNER.getDefaultState().withProperty(BlockBanner.FACING, facing));
		TileEntity newtile = world.getTileEntity(target);
		if (newtile instanceof TileEntityBanner) {
			((TileEntityBanner) newtile).setItemValues(stack, false);
		}
	}

	public static void changeBannerWall(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		BlockPos target = pos.down();
		int rotation;
		EnumFacing facing = state.getValue(BlockHorizontal.FACING);
		if (facing == EnumFacing.NORTH) {
			rotation = 8;
		}
		else if (facing == EnumFacing.SOUTH) {
			rotation = 0;
		}
		else if (facing == EnumFacing.EAST) {
			rotation = 12;
		}
		else if (facing == EnumFacing.WEST) {
			rotation = 4;
		}
		else {
			return;
		}
		if (!Blocks.STANDING_BANNER.canPlaceBlockAt(world, target)) return;
		if (!world.getBlockState(target).getMaterial().isReplaceable()) return;
		if (!isSideSolid(world, target, state, EnumFacing.DOWN)) return;
		TileEntity tile = world.getTileEntity(target);
		if (!(tile instanceof TileEntityBanner)) return;
		ItemStack stack = ((TileEntityBanner) tile).getItem();
		world.setBlockToAir(pos);
		world.setBlockState(target, Blocks.STANDING_BANNER.getDefaultState().withProperty(BlockBanner.ROTATION, Integer.valueOf(rotation)));
		TileEntity newtile = world.getTileEntity(target);
		if (newtile instanceof TileEntityBanner) {
			((TileEntityBanner) newtile).setItemValues(stack, false);
		}
	}

	public static void invertBed(World world, BlockPos pos) {
		if (world.isRemote) return;
		IBlockState state = world.getBlockState(pos);
		EnumFacing facing = state.getValue(BlockHorizontal.FACING);
		BlockPos pairpos = getBedPairPos(pos, state);
		if (Objects.isNull(pairpos)) return;
		IBlockState pairstate = world.getBlockState(pairpos);
		if (pairstate.getBlock() != state.getBlock()) return;
		IBlockState newstate1 = state.withProperty(BlockHorizontal.FACING, facing.getOpposite()).cycleProperty(BlockBed.PART);
		IBlockState newstate2 = pairstate.withProperty(BlockHorizontal.FACING, facing.getOpposite()).cycleProperty(BlockBed.PART);
		world.setBlockState(pos, newstate1);
		world.setBlockState(pairpos, newstate2);
	}

	public static void rotateBed(World world, BlockPos pos) {
		if (world.isRemote) return;
		IBlockState state = world.getBlockState(pos);
		EnumFacing facing = state.getValue(BlockHorizontal.FACING);
		BlockPos pairpos = getBedPairPos(pos, state);
		if (Objects.isNull(pairpos)) return;
		IBlockState pairstate = world.getBlockState(pairpos);
		if (pairstate.getBlock() != state.getBlock()) return;
		EnumFacing next = facing;
		BlockPos targetpos;
		do {
			next = FacingHelper.rotateY(next);
			if (state.getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD) {
				targetpos = pos.offset(next.getOpposite());
			} else {
				targetpos = pos.offset(next);
			}
		} while (!world.getBlockState(targetpos).getMaterial().isReplaceable() && next != facing);
		if (next == facing) return;
		IBlockState newstate1 = state.withProperty(BlockHorizontal.FACING, next);
		IBlockState newstate2 = newstate1.cycleProperty(BlockBed.PART);
		world.setBlockState(pos, newstate1);
		world.setBlockState(targetpos, newstate2);
		world.setBlockToAir(pairpos);
	}

	private static BlockPos getBedPairPos(BlockPos pos, IBlockState state) {
		if (state.getBlock() instanceof BlockBed) {
			EnumFacing facing = state.getValue(BlockHorizontal.FACING);
			BlockPos pairpos;
			if (state.getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD) {
				pairpos = pos.offset(facing.getOpposite());
			} else {
				pairpos = pos.offset(facing);
			}
			return pairpos;
		}
		return null;
	}

	// 覚書：BlockEnderChestはBlockChestを継承していないため大丈夫、のはず。他modのチェストまでは知らぬ……。
	public static void invertChest(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		BlockPos cpos = null;
		for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
			BlockPos hpos = pos.offset(enumfacing);
			if (world.getBlockState(hpos).getBlock() == block) {
				cpos = hpos;
			}
		}
		invertFacing(world, pos);
		if (cpos != null) {
			invertFacing(world, cpos);
			return;
		}
	}

	public static void rotateChest(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		BlockPos cpos = null;
		for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
			BlockPos hpos = pos.offset(enumfacing);
			if (world.getBlockState(hpos).getBlock() == block) {
				cpos = hpos;
			}
		}
		// ダブルチェストの場合は反転
		if (cpos != null) {
			invertFacing(world, pos);
			invertFacing(world, cpos);
		} else {
			rotateFacing(world, pos, EnumFacing.UP);
		}
	}

	public static void rotateDoor(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		BlockDoor.EnumDoorHalf doorhalf = state.getValue(BlockDoor.HALF);
		BlockPos pairpos = doorhalf == BlockDoor.EnumDoorHalf.UPPER? pos.down() : pos.up();
		rotateFacing(world, pos, EnumFacing.UP);
		rotateFacing(world, pairpos, EnumFacing.UP);
	}

	public static void invertDoor(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		BlockDoor.EnumDoorHalf doorhalf = state.getValue(BlockDoor.HALF);
		BlockPos pairpos = doorhalf == BlockDoor.EnumDoorHalf.UPPER? pos.down() : pos.up();
		cycleProperty(world, pos, BlockDoor.HINGE);
		cycleProperty(world, pairpos, BlockDoor.HINGE);
	}

	public static void changeSignStanding(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		int rotation = state.getValue(BlockBanner.ROTATION);
		EnumFacing facing;
		if (rotation == 0) {
			facing = EnumFacing.SOUTH;
		} else if (rotation == 4) {
			facing = EnumFacing.WEST;
		} else if (rotation == 8) {
			facing = EnumFacing.NORTH;
		} else if (rotation == 12) {
			facing = EnumFacing.EAST;
		} else {
			return;
		}
		TileEntity tile1 = world.getTileEntity(pos);
		if (!(tile1 instanceof TileEntitySign)) return;
		NBTTagCompound signtag = tile1.writeToNBT(new NBTTagCompound());
		if (!isSideSolid(world, pos, state, facing.getOpposite())) return;
		world.setBlockState(pos, Blocks.WALL_SIGN.getDefaultState().withProperty(BlockWallSign.FACING, facing));
		TileEntity tile2 = world.getTileEntity(pos);
		if (tile2 instanceof TileEntitySign) {
			tile2.readFromNBT(signtag);
		}
	}

	public static void changeSignWall(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		int rotation;
		EnumFacing facing = state.getValue(BlockHorizontal.FACING);
		if (facing == EnumFacing.NORTH) {
			rotation = 8;
		}
		else if (facing == EnumFacing.SOUTH) {
			rotation = 0;
		}
		else if (facing == EnumFacing.EAST) {
			rotation = 12;
		}
		else if (facing == EnumFacing.WEST) {
			rotation = 4;
		}
		else {
			return;
		}
		TileEntity tile1 = world.getTileEntity(pos);
		if (!(tile1 instanceof TileEntitySign)) return;
		NBTTagCompound signtag = tile1.writeToNBT(new NBTTagCompound());
		if (!isSideSolid(world, pos, state, EnumFacing.DOWN)) return;
		world.setBlockToAir(pos);
		world.setBlockState(pos, Blocks.STANDING_SIGN.getDefaultState().withProperty(BlockStandingSign.ROTATION, Integer.valueOf(rotation)));
		TileEntity tile2 = world.getTileEntity(pos);
		if (tile2 instanceof TileEntitySign) {
			tile2.readFromNBT(signtag);
		}
	}

	public static IProperty<?> getTypeContainer(IBlockState state) {
		for (IProperty<?> property : state.getPropertyKeys()) {
			if (property.getName().equals("type")) {
				return property;
			}
		}
		return null;
	}

	public static void openEndPortal(World world, BlockPos pos) {
		BlockPattern gatePattern = FactoryBlockPattern.start().aisle(
				"?EEE?",
				"EAAAE",
				"EAAAE",
				"EAAAE",
				"?EEE?")
				.where('?', BlockWorldState.hasState(BlockStateMatcher.ANY))
				.where('E', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.END_PORTAL_FRAME)
						.where(BlockEndPortalFrame.EYE, (eye -> eye))))
				.where('A', BlockWorldState.hasState(wstate -> wstate.getMaterial().isReplaceable()))
				.build();
		BlockPattern.PatternHelper gate = gatePattern.match(world, pos);
		if (gate != null) {
			BlockPos blockpos = gate.getFrontTopLeft();
			EnumFacing facing = gate.getUp();
			BlockPos center = blockpos.offset(facing.getOpposite(), 2).offset(FacingHelper.rotateY(facing), 2);
			if (world.isRemote) {
				world.playSound(center.getX(), center.getY(), center.getZ(), SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
			} else {
				for (int j = -2; j < 3; ++j) {
					for (int k = -2; k < 3; ++k) {
						BlockPos target = center.add(j, 0, k);
						IBlockState portal = Blocks.END_PORTAL_FRAME.getDefaultState();
						IBlockState eye = portal.withProperty(BlockEndPortalFrame.EYE, true);
						IBlockState north = eye.withProperty(BlockHorizontal.FACING, EnumFacing.NORTH);
						IBlockState south = eye.withProperty(BlockHorizontal.FACING, EnumFacing.SOUTH);
						IBlockState east = eye.withProperty(BlockHorizontal.FACING, EnumFacing.EAST);
						IBlockState west = eye.withProperty(BlockHorizontal.FACING, EnumFacing.WEST);
						if (j == -2) {
							if (k != -2 && k != 2) {
								world.setBlockState(target, east);
							}
						} else if (j == 2) {
							if (k != -2 && k != 2) {
								world.setBlockState(target, west);
							}
						} else {
							if (k == -2) {
								world.setBlockState(target, south);
							} else if (k == 2) {
								world.setBlockState(target, north);
							} else {
								world.setBlockState(target, Blocks.END_PORTAL.getDefaultState());
							}
						}
					}
				}
			}
		} else {
			invertFacing(world, pos);
		}
	}

}
