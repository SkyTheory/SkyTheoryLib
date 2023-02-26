package skytheory.lib.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class RunOneSingleMinded<T extends LivingEntity> extends RunOnePrioritized<T> {

	@Override
	protected void checkRunningBehaviors(ServerLevel pLevel, T pEntity, long pGameTime) {
		if (getRunningBehaviors().isEmpty()) {
			super.checkRunningBehaviors(pLevel, pEntity, pGameTime);
		}
	}
	
}
