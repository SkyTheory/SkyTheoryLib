package skytheory.lib.entity.ai.behavior;

import java.util.Collection;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.Behavior.Status;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

/**
 * 複数のBehaviorから選択的に処理を行うためのBehaviorControl<br>
 * getAllBehaviorsで得られるBehaviorのうち、BehaviorControl#getStatusがStatus.RUNNINGであるものを実行する<br>
 * tryStartのタイミングでupdateBehaviorConditionsが発火するので、その際にステータスを更新すること
 * @author SkyTheory
 */
public abstract class BehaviorSelector<T extends LivingEntity> implements BehaviorControl<T> {
	
	protected Behavior.Status status = Behavior.Status.STOPPED;

	public Behavior.Status getStatus() {
		return this.status;
	}

	protected abstract Collection<BehaviorControl<? super T>> getAllBehaviors();
	
	/**
	 * tryStartのタイミングで実行される
	 * tickさせたいBehaviorのStatusをRUNNINGにしておくこと
	 * @param pLevel
	 * @param pEntity
	 * @param pGameTime
	 */
	protected abstract void updateBehaviorStatus(ServerLevel pLevel, T pEntity, long pGameTime);

	public boolean tryStart(ServerLevel pLevel, T pEntity, long pGameTime) {
		this.updateBehaviorStatus(pLevel, pEntity, pGameTime);
		boolean running = getAllBehaviors().stream().anyMatch(behavior -> behavior.getStatus() == Status.RUNNING);
		this.status = running ? Status.RUNNING : Status.STOPPED;
		return running;
	}
	public void tickOrStop(ServerLevel pLevel, T pEntity, long pGameTime) {
		this.getAllBehaviors().stream()
		.filter((behavior) -> behavior.getStatus() == Behavior.Status.RUNNING)
		.forEach((behavior) -> behavior.tickOrStop(pLevel, pEntity, pGameTime));
		
		// tryStartの再判定用にStatusを更新する
		this.status = Behavior.Status.STOPPED;
	}

	public void doStop(ServerLevel pLevel, T pEntity, long pGameTime) {
		this.getAllBehaviors().stream()
		.filter((behavior) -> behavior.getStatus() == Behavior.Status.RUNNING)
		.forEach((behavior) -> behavior.doStop(pLevel, pEntity, pGameTime));
		this.status = Behavior.Status.STOPPED;
	}

	public String debugString() {
		return this.getClass().getSimpleName();
	}

}