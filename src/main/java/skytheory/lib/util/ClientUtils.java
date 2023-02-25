package skytheory.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientUtils {

	@SuppressWarnings("resource")
	public static Player getPlayer() {
		return Minecraft.getInstance().player;
	}
	
}
