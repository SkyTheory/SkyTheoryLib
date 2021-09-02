package skytheory.lib.tile;

import java.util.List;

import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import skytheory.lib.capability.datasync.DataSyncHandler;
import skytheory.lib.capability.datasync.IDataSync;
import skytheory.lib.config.Config;
import skytheory.lib.network.tile.TileSync;
import skytheory.lib.plugin.waila.IWailaTipTile;
import skytheory.lib.util.EnumRotation;
import skytheory.lib.util.EnumSide;
import skytheory.lib.util.FacingUtils;
import skytheory.lib.util.STLibConstants;
import skytheory.lib.util.TextUtils;

/**
 * 24方向の向きを持つTileEntity<br>
 * ItemBlockRotationalなどとセットにして使うこと
 * @author SkyTheory
 *
 */
public abstract class TileRotational extends TileEntity implements ISidedTileRotational, IDataSync, IWailaTipTile {

	public static String KEY_ROTATION = "Rotation";

	private EnumRotation rotation = EnumRotation.NORTH_DOWN_WEST;

	@Override
	public void setRotation(EnumRotation rotation) {
		this.rotation = rotation;
		this.markDirty();
		if (!this.world.isRemote) {
			TileSync.sendToClient(this, DataSyncHandler.SYNC_DATA_CAPABILITY, FacingUtils.SET_SINGLE_NULL);
		}
	}

	@Override
	public EnumRotation getRotation() {
		return rotation;
	}

	@Override
	public NBTTagCompound serializeSync() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString(KEY_ROTATION, rotation.getName());
		return nbt;
	}

	@Override
	public void deserializeSync(NBTTagCompound compound) {
		if (compound.hasKey(KEY_ROTATION, Constants.NBT.TAG_STRING)) {
			String name = compound.getString(KEY_ROTATION);
			EnumRotation rotation = EnumRotation.fromName(name);
			if (rotation != null) {
				this.rotation = rotation;
			}
		}
	}

	@Override
	public void getWailaTips(ItemStack stack, List<String> tips, IWailaDataAccessor accessor) {
		tips.add(TextUtils.format(STLibConstants.TIP_FACING, this.getFacing(EnumSide.FRONT).getName()));
		tips.add(TextUtils.format(STLibConstants.TIP_SIDE, this.getSide(accessor.getSide()).getName()));
		if (Config.debug_tips) {
			tips.add(rotation.getName());
		}
	}
}
