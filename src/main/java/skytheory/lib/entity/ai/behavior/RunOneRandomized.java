package skytheory.lib.entity.ai.behavior;

import java.util.Collection;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior.Status;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

public class RunOneRandomized<T extends LivingEntity> extends AbstractRunOneBehavior<T> {

	protected final ShufflingList<BehaviorControl<? super T>> allBehaviors;

	public RunOneRandomized() {
		this.allBehaviors = new ShufflingList<>();
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
	public void updateBehaviorStatus(ServerLevel pLevel, T pEntity, long pGameTime) {
		if (this.isRunning()) return;
		this.allBehaviors.shuffle();
		for (var behavior : allBehaviors) {
			behavior.tryStart(pLevel, pEntity, pGameTime);
			if (behavior.getStatus() == Status.RUNNING) {
				break;
			}
		}
	}

	@Override
	protected Collection<BehaviorControl<? super T>> getAllBehaviors() {
		return this.allBehaviors.stream().toList();
	}

}
