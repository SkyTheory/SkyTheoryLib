package skytheory.lib.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Predicate;

import com.mojang.logging.LogUtils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior.Status;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

public abstract class AbstractRunOneBehavior<T extends LivingEntity> extends BehaviorSelector<T> {

	@Override
	public void tickOrStop(ServerLevel pLevel, T pEntity, long pGameTime) {
		long count = this.getAllBehaviors().stream()
				.filter(behavior -> behavior.getStatus() == Status.RUNNING)
				.count();
		if (count > 1) {
			LogUtils.getLogger().warn("Illegal state behaviors for : " + this.getClass().getCanonicalName());
			this.getAllBehaviors().stream()
			.filter(behavior -> behavior.getStatus() == Status.RUNNING)
			.findFirst()
			.ifPresent(behavior -> stopOther(behavior, pLevel, pEntity, pGameTime));
		}
		super.tickOrStop(pLevel, pEntity, pGameTime);
	}
	
	protected void stopOther(BehaviorControl<? super T> pBehavior, ServerLevel pLevel, T pEntity, long pGameTime) {
		this.getAllBehaviors().stream()
		.filter(Predicate.not(pBehavior::equals))
		.filter(behavior -> behavior.getStatus() == Status.RUNNING)
		.forEach(behavior -> behavior.doStop(pLevel, pEntity, pGameTime));
	}
	
	protected Optional<BehaviorControl<? super T>> getCurrentBehavior() {
		return this.getAllBehaviors().stream()
		.filter(behavior -> behavior.getStatus() == Status.RUNNING)
		.findFirst();
	}

	protected boolean isRunning() {
		return !this.getAllBehaviors().stream()
		.noneMatch(behavior -> behavior.getStatus() == Status.RUNNING);
	}
	
}
