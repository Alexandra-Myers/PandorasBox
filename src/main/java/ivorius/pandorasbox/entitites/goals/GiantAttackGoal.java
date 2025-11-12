package ivorius.pandorasbox.entitites.goals;

import ivorius.pandorasbox.entitites.FunctionalGiant;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class GiantAttackGoal extends MeleeAttackGoal {
	private final FunctionalGiant giant;
	private int raiseArmTicks;

	public GiantAttackGoal(FunctionalGiant giant, double d, boolean bl) {
		super(giant, d, bl);
		this.giant = giant;
	}

	@Override
	public void start() {
		super.start();
		this.raiseArmTicks = 0;
	}

	@Override
	public void stop() {
		super.stop();
		this.giant.setAggressive(false);
	}

	@Override
	public void tick() {
		super.tick();
		this.raiseArmTicks++;
        this.giant.setAggressive(this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2);
	}
}