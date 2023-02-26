package skytheory.lib.entity.ai.behavior;

import java.util.Collection;

import com.google.common.base.Predicates;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.Behavior.Status;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

public abstract class BehaviorSelector<T extends LivingEntity> implements BehaviorControl<T> {
	
	private Behavior.Status status = Behavior.Status.STOPPED;

	public Behavior.Status getStatus() {
		return this.status;
	}

	/**
	 * tryStart時に発火し、実行するBehaviorをgetRunningBehaviorsに登録する
	 */
	protected abstract void checkRunningBehaviors(ServerLevel pLevel, T pEntity, long pGameTime);

	/**
	 * 実行するべきBehaviorを取得する
	 */
	protected abstract Collection<BehaviorControl<? super T>> getRunningBehaviors();
	
	/**
	 * 使用する全Behaviorを取得する
	 */
	protected abstract Collection<BehaviorControl<? super T>> getAllBehaviors();
	
	/**
	 * getRunningBehaviorで得られたコレクションからBehaviorを除去する
	 */
	protected void removeRunningBehavior(BehaviorControl<? super T> behavior) {
		getRunningBehaviors().remove(behavior);
	}

	public boolean tryStart(ServerLevel pLevel, T pEntity, long pGameTime) {
		
		// 実行するべきBehaviorを取得する
		this.checkRunningBehaviors(pLevel, pEntity, pGameTime);
		if (!this.getRunningBehaviors().isEmpty()) {
			this.status = Status.RUNNING;
			return true;
		}
		return false;
	}

	public void tickOrStop(ServerLevel pLevel, T pEntity, long pGameTime) {
		
		// getRunningBehaviorsで取得したBehaviorをtickさせる
		this.getRunningBehaviors().stream()
		.filter((behavior) -> behavior.getStatus() == Behavior.Status.RUNNING)
		.forEach((behavior) -> behavior.tickOrStop(pLevel, pEntity, pGameTime));
		
		// getRunningBehaviorに含まれないBehaviorを停止させる
		this.getAllBehaviors().stream()
		.filter(Predicates.not(this.getRunningBehaviors()::contains))
		.filter(behavior -> behavior.getStatus() == Status.RUNNING)
		.forEach(behavior -> behavior.doStop(pLevel, pEntity, pGameTime));
		
		// 停止したBehaviorをリストから除去する
		this.getAllBehaviors().stream()
		.filter(this.getRunningBehaviors()::contains)
		.filter(behavior -> behavior.getStatus() == Status.STOPPED)
		.forEach(this::removeRunningBehavior);
		
		// tryStartの再判定用にStatusを更新する
		this.status = Behavior.Status.STOPPED;
	}

	public void doStop(ServerLevel pLevel, T pEntity, long pGameTime) {
		this.status = Behavior.Status.STOPPED;
		this.getRunningBehaviors().stream()
		.filter((behavior) -> behavior.getStatus() == Behavior.Status.RUNNING)
		.peek((behavior) -> behavior.doStop(pLevel, pEntity, pGameTime))
		.forEach(this::removeRunningBehavior);
	}

	public String debugString() {
		return this.getClass().getSimpleName();
	}

}