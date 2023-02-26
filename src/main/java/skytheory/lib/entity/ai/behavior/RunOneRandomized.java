package skytheory.lib.entity.ai.behavior;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

public class RunOneRandomized<T extends LivingEntity> extends BehaviorSelector<T> {

	protected final ShufflingList<BehaviorControl<? super T>> allBehaviors;
	
	protected Collection<BehaviorControl<? super T>> runningBehavior;
	
	public RunOneRandomized() {
		this.allBehaviors = new ShufflingList<>();
		this.runningBehavior = Collections.emptySet();
	}
	
	public RunOneRandomized<T> addBehavior(BehaviorSelector<? super T> pBehavior, int pWeight) {
		this.allBehaviors.add(pBehavior, pWeight);
		return this;
	}
	
	@Override
	protected Collection<BehaviorControl<? super T>> getRunningBehaviors() {
		return this.runningBehavior;
	}

	@Override
	protected void checkRunningBehaviors(ServerLevel pLevel, T pEntity, long pGameTime) {
		if (this.getRunningBehaviors().isEmpty()) {
			this.allBehaviors.shuffle();
			for (var behavior : allBehaviors) {
				if (behavior.tryStart(pLevel, pEntity, pGameTime)) {
					this.runningBehavior = Collections.singleton(behavior);
					return;
				}
			}
		}
	}

	@Override
	protected Collection<BehaviorControl<? super T>> getAllBehaviors() {
		return this.allBehaviors.stream().toList();
	}

}
