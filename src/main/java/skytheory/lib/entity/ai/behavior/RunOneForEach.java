package skytheory.lib.entity.ai.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

// TODO Historyの中身をMemoryで保持できるように
public class RunOneForEach<T extends LivingEntity> extends BehaviorSelector<T> {

	protected final List<BehaviorControl<? super T>> allBehaviors;
	protected final List<Integer> history;

	protected Collection<BehaviorControl<? super T>> runningBehavior;

	public RunOneForEach() {
		this.allBehaviors = new ArrayList<>();
		this.history = new LinkedList<>();
		this.runningBehavior = Collections.emptySet();
	}
	
	public RunOneForEach(Collection<BehaviorControl<? super T>> behaviors) {
		this();
		behaviors.forEach(this::addBehavior);
	}

	public RunOneForEach<T> addBehavior(BehaviorControl<? super T> pBehaviorControl) {
		this.allBehaviors.add(pBehaviorControl);
		return this;
	}

	@Override
	protected void checkRunningBehaviors(ServerLevel pLevel, T pEntity, long pGameTime) {
		if (this.runningBehavior.isEmpty()) {
			var sorted = allBehaviors.stream()
					.sorted(Comparator.comparingInt(behavior -> history.indexOf(allBehaviors.indexOf(behavior))))
					.toList();
			for (var behavior : sorted) {
				if (behavior.tryStart(pLevel, pEntity, pGameTime)) {
					this.setRunningBehavior(behavior);
					return;
				}
			}
		}
	}

	@Override
	protected Collection<BehaviorControl<? super T>> getRunningBehaviors() {
		return runningBehavior;
	}

	@Override
	protected Collection<BehaviorControl<? super T>> getAllBehaviors() {
		return allBehaviors;
	}

	protected void setRunningBehavior(BehaviorControl<? super T> behavior) {
		this.runningBehavior = Collections.singleton(behavior);
		int index = allBehaviors.indexOf(behavior);
		if (history.contains(index)) {
			this.history.remove(index);
			this.history.add(index);
		}
	}

	@Override
	protected void removeRunningBehavior(BehaviorControl<? super T> behavior) {
		this.runningBehavior = Collections.emptySet();
	}

}
