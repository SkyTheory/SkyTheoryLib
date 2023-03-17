package skytheory.lib.entity.ai.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior.Status;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

public class RunOnePrioritized<T extends LivingEntity> extends BehaviorSelector<T> {

	protected final List<BehaviorControl<? super T>> allBehaviors;
	protected final boolean continuity;

	protected Optional<BehaviorControl<? super T>> currentBehavior;

	public RunOnePrioritized() {
		this(false);
	}
	
	public RunOnePrioritized(boolean continuity) {
		this.allBehaviors = new ArrayList<>();
		this.currentBehavior = Optional.empty();
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
	public boolean tryStart(ServerLevel pLevel, T pEntity, long pGameTime) {
		if (this.isRunning() && this.continuity) return true;
		for (var behavior : allBehaviors) {
			if (this.isRunning() && behavior == this.currentBehavior.get()) break;
			behavior.tryStart(pLevel, pEntity, pGameTime);
			if (behavior.getStatus() == Status.RUNNING) {
				this.currentBehavior = Optional.of(behavior);
				break;
			}
		}
		boolean running = this.isRunning();
		this.status = running ? Status.RUNNING : Status.STOPPED;
		return running;
	}

	@Override
	protected Collection<BehaviorControl<? super T>> getAllBehaviors() {
		return allBehaviors;
	}

	@Override
	protected void onBehaviorStopped(BehaviorControl<? super T> behavior) {
		this.currentBehavior = Optional.empty();
	}
	
	protected boolean isRunning() {
		return this.currentBehavior.map(behavior -> behavior.getStatus() == Status.RUNNING).orElse(false);
	}
	
}
