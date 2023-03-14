package skytheory.lib.entity.ai.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior.Status;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

public class RunOnePrioritized<T extends LivingEntity> extends BehaviorSelector<T> {

	protected final List<BehaviorControl<? super T>> allBehaviors;
	protected final boolean continuity;
	
	protected Collection<BehaviorControl<? super T>> runningBehavior;

	public RunOnePrioritized() {
		this(false);
	}
	
	public RunOnePrioritized(boolean continuity) {
		this.allBehaviors = new ArrayList<>();
		this.runningBehavior = Collections.emptySet();
		this.continuity = continuity;
	}

	public RunOnePrioritized(List<BehaviorControl<? super T>> behaviors) {
		this(behaviors, false);
	}
	
	public RunOnePrioritized(List<BehaviorControl<? super T>> behaviors, boolean continuity) {
		this(false);
		behaviors.forEach(this::addBehavior);
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
		Optional<BehaviorControl<? super T>> currentBehavior = this.runningBehavior.stream().findFirst();
		boolean isRunning = currentBehavior.map(behavior -> behavior.getStatus() == Status.RUNNING).orElse(false);
		if (isRunning && this.continuity) return;
		for (var behavior : allBehaviors) {
			if (isRunning && behavior == currentBehavior.orElse(null)) break;
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
		if (this.runningBehavior.contains(behavior)) {
			this.runningBehavior = Collections.emptySet();
		}
	}

}
