package skytheory.lib.entity.ai.behavior;

import java.util.Collection;
import java.util.Optional;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior.Status;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

public class RunOneRandomized<T extends LivingEntity> extends BehaviorSelector<T> {

	protected final ShufflingList<BehaviorControl<? super T>> allBehaviors;
	protected Optional<BehaviorControl<? super T>> currentBehavior;

	public RunOneRandomized() {
		this.allBehaviors = new ShufflingList<>();
		this.currentBehavior = Optional.empty();
	}

	public RunOneRandomized(Collection<BehaviorControl<? super T>> behaviors) {
		this();
		behaviors.forEach(behavior -> this.addBehavior(behavior, 1));
	}

	public RunOneRandomized<T> addBehavior(BehaviorControl<? super T> pBehavior) {
		this.allBehaviors.add(pBehavior, 1);
		return this;
	}
	
	public RunOneRandomized<T> addBehavior(BehaviorControl<? super T> pBehavior, int pWeight) {
		this.allBehaviors.add(pBehavior, pWeight);
		return this;
	}

	@Override
	public boolean tryStart(ServerLevel pLevel, T pEntity, long pGameTime) {
		if (this.isRunning()) return true;
		this.allBehaviors.shuffle();
		for (var behavior : allBehaviors) {
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
		return this.allBehaviors.stream().toList();
	}

	@Override
	protected void onBehaviorStopped(BehaviorControl<? super T> behavior) {
		this.currentBehavior = Optional.empty();
	}
	
	protected boolean isRunning() {
		return this.currentBehavior.map(behavior -> behavior.getStatus() == Status.RUNNING).orElse(false);
	}
	
}
