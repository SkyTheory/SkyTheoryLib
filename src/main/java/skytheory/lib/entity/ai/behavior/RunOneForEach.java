package skytheory.lib.entity.ai.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior.Status;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

public class RunOneForEach<T extends LivingEntity> extends AbstractRunOneBehavior<T> {

	protected final List<BehaviorControl<? super T>> allBehaviors;
	protected final List<Integer> history;

	public RunOneForEach() {
		this.allBehaviors = new ArrayList<>();
		this.history = new LinkedList<>();
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
	public void updateBehaviorStatus(ServerLevel pLevel, T pEntity, long pGameTime) {
		if (!this.isRunning()) {
			var sorted = allBehaviors.stream()
					.sorted(Comparator.comparingInt(behavior -> history.indexOf(allBehaviors.indexOf(behavior))))
					.toList();
			for (var behavior : sorted) {
				behavior.tryStart(pLevel, pEntity, pGameTime);
				if (behavior.getStatus() == Status.RUNNING) {
					setRunningBehavior(behavior, pLevel, pEntity, pGameTime);
					break;
				}
			}
		}
	}
	
	@Override
	protected Collection<BehaviorControl<? super T>> getAllBehaviors() {
		return allBehaviors;
	}

	protected void setRunningBehavior(BehaviorControl<? super T> pBehavior, ServerLevel pLevel, T pEntity, long pGameTime) {
		int index = allBehaviors.indexOf(pBehavior);
		if (history.contains(index)) {
			this.history.remove(index);
		}
		this.history.add(index);
		stopOther(pBehavior, pLevel, pEntity, pGameTime);
	}
	
}
