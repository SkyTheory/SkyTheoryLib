package skytheory.lib.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import skytheory.lib.block.IWrenchBlock;

public class WrenchTypes {

	/** 時計回りに回転または反転、スニークで反転 */
	public static final WrenchType ALL_FACINGS = new WrenchType("All_Facings") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.invertFacing(world, pos);
			} else {
				WrenchHelper.rotateFacing(world, pos, facing);
			}
		}
	};

	/** 水平方向に時計回りに回転、スニークで反転 */
	public static final WrenchType HORIZONTAL = new WrenchType("Horizontal") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.invertFacing(world, pos);
			} else {
				WrenchHelper.rotateFacing(world, pos, EnumFacing.UP);
			}
		}
	};

	/** クリックで手前に向ける、再度クリックで反転。ホッパーなどの五方向を向くブロック用 */
	public static final WrenchType QUINTET = new WrenchType("Quintet") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.invertFacing(world, pos);
			} else {
				WrenchHelper.turnFront(player, world, pos, facing);
			}
		}
	};

	/** 水平方向に時計回りに回転または反転、スニークで反転。松明など、支えを必要とするブロック用 */
	public static final WrenchType SOLID = new WrenchType("Solid") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.invertSolid(world, pos);
			} else {
				WrenchHelper.rotateNextSolid(world, pos, facing);
			}
		}
	};

	/** 水平方向に時計回りに回転または反転、スニークで反転。ボタンやはしごなどの壁掛けブロック用 */
	public static final WrenchType WALL = new WrenchType("Wall") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.invertSolid(world, pos);
			} else {
				WrenchHelper.rotateNextSolid(world, pos, EnumFacing.UP);
			}
		}
	};

	/** 向きをひとつ変更。modで追加されるかもしれない変則的な向きを持つブロック用 */
	public static final WrenchType CYCLE = new WrenchType("Cycle") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			WrenchHelper.cycleProperty(world, pos, WrenchHelper.getFacingContainer(world.getBlockState(pos)));
		}
	};

	/** 向きを変更。原木などの軸で方向を持つブロック用 */
	public static final WrenchType AXIS = new WrenchType("Axis") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			WrenchHelper.cycleProperty(world, pos, WrenchHelper.getAxisContainer(world.getBlockState(pos)));
		}
	};

	/** 回転または壁掛けに変更。バナー用 */
	public static final WrenchType BANNER_STANDING = new WrenchType("Banner_Standing") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.changeBannerStanding(world, pos);
			} else {
				WrenchHelper.cycleProperty(world, pos, BlockBanner.ROTATION);
			}
		}
	};

	/** 回転または床置きに変更。バナー用 */
	public static final WrenchType BANNER_WALL = new WrenchType("Banne_Wall") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.changeBannerWall(world, pos);
			} else {
				WrenchHelper.rotateNextSolid(world, pos, EnumFacing.UP);
			}
		}
	};

	/** 時計回りに回転または反転。ベッド用 */
	public static final WrenchType BED = new WrenchType("Bed") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.invertBed(world, pos);
			} else {
				WrenchHelper.rotateBed(world, pos);
			}
		}
	};

	/** 時計回りに回転または反転。チェスト用 */
	public static final WrenchType CHEST = new WrenchType("Chest") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.invertChest(world, pos);
			} else {
				WrenchHelper.rotateChest(world, pos);
			}
		}
	};

	/** 時計回りに回転または左右反転。ドア用 */
	public static final WrenchType DOOR = new WrenchType("Door") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.invertDoor(world, pos);
			} else {
				WrenchHelper.rotateDoor(world, pos);
			}
		}
	};

	/** HORIZONTALと同様、ただしスニーク時にエンダーアイが揃っていれば開通。エンドポータル用 */
	public static final WrenchType END_PORTAL = new WrenchType("End_Portal") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.openEndPortal(world, pos);
			} else {
				WrenchHelper.rotateFacing(world, pos, EnumFacing.UP);
			}
		}
	};

	/** BANNERと同様。こちらは看板用 */
	public static final WrenchType SIGN_STANDING = new WrenchType("Sign_Standing") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.changeSignStanding(world, pos);
			} else {
				WrenchHelper.cycleProperty(world, pos, BlockBanner.ROTATION);
			}
		}
	};

	/** BANNERと同様。こちらは看板用 */
	public static final WrenchType SIGN_WALL = new WrenchType("Sign_Wall") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.rotateNextSolid(world, pos, EnumFacing.UP);
			} else {
				WrenchHelper.cycleProperty(world, pos, BlockBanner.ROTATION);
			}
		}
	};

	/** ハーフブロックの上下を反転 */
	public static final WrenchType SLAB = new WrenchType("Slab") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			WrenchHelper.cycleProperty(world, pos, BlockSlab.HALF);
		}
	};

	/** 時計回りに回転、スニークで上下反転。階段用 */
	public static final WrenchType STAIRS = new WrenchType("Stairs") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.cycleProperty(world, pos, BlockStairs.HALF);
			} else {
				WrenchHelper.rotateFacing(world, pos, EnumFacing.UP);
			}
		}
	};

	/** 時計回りに回転、スニークで上下反転。トラップドア用 */
	public static final WrenchType TRAP_DOOR = new WrenchType("Trap_Door") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			if (player.isSneaking()) {
				WrenchHelper.cycleProperty(world, pos, BlockTrapDoor.HALF);
			} else {
				WrenchHelper.rotateFacing(world, pos, EnumFacing.UP);
			}
		}
	};

	/** IWrenchBlockを継承したブロック用 */
	public static final WrenchType WRENCH_BLOCK = new WrenchType("Wrench_Block") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof IWrenchBlock) {
				((IWrenchBlock) block).onRightClickWithWrench(player, world, pos, hand, facing);
			}
		}
	};

	/** ブラックリスト。ピストンの頭など、回転させるべきでないものを登録する */
	public static final WrenchType NONE = new WrenchType("Prohibited") {
		@Override
		public void interact(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
		}

		@Override
		public boolean skipActivateBlock(EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
			return false;
		}
	};
}
