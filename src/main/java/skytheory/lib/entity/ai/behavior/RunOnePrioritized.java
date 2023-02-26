package skytheory.lib.entity.ai.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

public class RunOnePrioritized<T extends LivingEntity> extends BehaviorSelector<T> {

	protected final List<BehaviorControl<? super T>> allBehaviors;

	protected Collection<BehaviorControl<? super T>> runningBehavior;

	// TODO Interuputの実装
	public RunOnePrioritized() {
		this.allBehaviors = new ArrayList<>();
		this.runningBehavior = Collections.emptySet();
	}

	public RunOnePrioritized<T> addBehavior(BehaviorControl<? super T> pBehaviorControl) {
		this.allBehaviors.add(pBehaviorControl);
		return this;
	}

	@Override
	protected Collection<BehaviorControl<? super T>> getRunningBehaviors() {
		return runningBehavior;
	}

	@Override
	protected void checkRunningBehaviors(ServerLevel pLevel, T pEntity, long pGameTime) {
		for (var behavior : allBehaviors) {
			if (behavior.tryStart(pLevel, pEntity, pGameTime)) {
				setRunningBehavior(behavior);
				return;
			}
		}
	}

	@Override
	protected Collection<BehaviorControl<? super T>> getAllBehaviors() {
		return allBehaviors;
	}

	protected void setRunningBehavior(BehaviorControl<? super T> behavior) {
		this.runningBehavior = Collections.singleton(behavior);
	}

	@Override
	protected void removeRunningBehavior(BehaviorControl<? super T> behavior) {
		this.runningBehavior = Collections.emptySet();
	}

}
