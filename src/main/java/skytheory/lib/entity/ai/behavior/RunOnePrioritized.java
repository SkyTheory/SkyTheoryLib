package skytheory.lib.entity.ai.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior.Status;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

public class RunOnePrioritized<T extends LivingEntity> extends AbstractRunOneBehavior<T> {

	protected final List<BehaviorControl<? super T>> allBehaviors;
	protected final boolean continuity;

	public RunOnePrioritized() {
		this(false);
	}
	
	public RunOnePrioritized(boolean continuity) {
		this.allBehaviors = new ArrayList<>();
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
	protected Collection<BehaviorControl<? super T>> getAllBehaviors() {
		return allBehaviors;
	}

	@Override
	protected void updateBehaviorStatus(ServerLevel pLevel, T pEntity, long pGameTime) {
		if (this.isRunning() && this.continuity) return;
		for (var behavior : allBehaviors) {
			if (this.getCurrentBehavior().map(behavior::equals).orElse(false)) break;
			behavior.tryStart(pLevel, pEntity, pGameTime);
			if (behavior.getStatus() == Status.RUNNING) {
				this.stopOther(behavior, pLevel, pEntity, pGameTime);
				break;
			}
		}
	}
	
}
