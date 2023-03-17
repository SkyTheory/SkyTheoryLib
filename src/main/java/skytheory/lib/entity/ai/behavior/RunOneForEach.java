package skytheory.lib.entity.ai.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior.Status;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

public class RunOneForEach<T extends LivingEntity> extends BehaviorSelector<T> {

	protected final List<BehaviorControl<? super T>> allBehaviors;
	protected final List<Integer> history;

	protected Optional<BehaviorControl<? super T>> currentBehavior;

	public RunOneForEach() {
		this.allBehaviors = new ArrayList<>();
		this.history = new LinkedList<>();
		this.currentBehavior = Optional.empty();
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
	public boolean tryStart(ServerLevel pLevel, T pEntity, long pGameTime) {
		if (!this.isRunning()) {
			var sorted = allBehaviors.stream()
					.sorted(Comparator.comparingInt(behavior -> history.indexOf(allBehaviors.indexOf(behavior))))
					.toList();
			for (var behavior : sorted) {
				behavior.tryStart(pLevel, pEntity, pGameTime);
				if (behavior.getStatus() == Status.RUNNING) {
					setRunningBehavior(behavior);
					break;
				}
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

	protected void setRunningBehavior(BehaviorControl<? super T> behavior) {
		this.currentBehavior = Optional.of(behavior);
		int index = allBehaviors.indexOf(behavior);
		if (history.contains(index)) {
			this.history.remove(index);
		}
		this.history.add(index);
	}

	@Override
	protected void onBehaviorStopped(BehaviorControl<? super T> behavior) {
		this.currentBehavior = Optional.empty();
	}

	protected boolean isRunning() {
		return this.currentBehavior.map(behavior -> behavior.getStatus() == Status.RUNNING).orElse(false);
	}
	
}
