package skytheory.lib.entity.ai.behavior;

import java.util.Collection;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.Behavior.Status;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

public abstract class BehaviorSelector<T extends LivingEntity> implements BehaviorControl<T> {
	
	protected Behavior.Status status = Behavior.Status.STOPPED;

	public Behavior.Status getStatus() {
		return this.status;
	}

	protected abstract Collection<BehaviorControl<? super T>> getAllBehaviors();

	protected abstract void onBehaviorStopped(BehaviorControl<? super T> behavior);

	public void tickOrStop(ServerLevel pLevel, T pEntity, long pGameTime) {
		
		this.getAllBehaviors().stream()
		.filter((behavior) -> behavior.getStatus() == Behavior.Status.RUNNING)
		.forEach((behavior) -> behavior.tickOrStop(pLevel, pEntity, pGameTime));
				
		// Behaviorの停止を通知する
		this.getAllBehaviors().stream()
		.filter(behavior -> behavior.getStatus() == Status.STOPPED)
		.forEach(this::onBehaviorStopped);
		
		// tryStartの再判定用にStatusを更新する
		this.status = Behavior.Status.STOPPED;
	}

	public void doStop(ServerLevel pLevel, T pEntity, long pGameTime) {
		this.getAllBehaviors().stream()
		.filter((behavior) -> behavior.getStatus() == Behavior.Status.RUNNING)
		.peek((behavior) -> behavior.doStop(pLevel, pEntity, pGameTime))
		.forEach(this::onBehaviorStopped);
		this.status = Behavior.Status.STOPPED;
	}

	public String debugString() {
		return this.getClass().getSimpleName();
	}

}