package com.natsu.backport.common.entity.goal;

import java.util.EnumSet;

import javax.annotation.Nullable;

import com.natsu.backport.common.item.SpearItem;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;

/**
 * Port of the 1.21.11 SpearUseGoal : approach with the spear couched, hold the
 * charge until it can deal damage, then break away and cool down at a distance.
 */
public class SpearUseGoal<T extends Monster> extends Goal {

	private static final int MIN_REPOSITION_DISTANCE = 6;
	private static final int MAX_REPOSITION_DISTANCE = 7;
	private static final int MIN_COOLDOWN_DISTANCE = 9;
	private static final int MAX_COOLDOWN_DISTANCE = 11;
	private static final int MAX_FLEEING_TIME = 100;

	private final T mob;
	@Nullable
	private SpearUseState state;
	private final double speedModifierWhenCharging;
	private final double speedModifierWhenRepositioning;
	private final float approachDistanceSq;
	private final float targetInRangeRadiusSq;

	public SpearUseGoal(T mob, double speedModifierWhenCharging, double speedModifierWhenRepositioning,
			float approachDistance, float targetInRangeRadius) {
		this.mob = mob;
		this.speedModifierWhenCharging = speedModifierWhenCharging;
		this.speedModifierWhenRepositioning = speedModifierWhenRepositioning;
		this.approachDistanceSq = approachDistance * approachDistance;
		this.targetInRangeRadiusSq = targetInRangeRadius * targetInRangeRadius;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		return this.ableToAttack() && !this.mob.isUsingItem();
	}

	private boolean ableToAttack() {
		return this.mob.getTarget() != null && this.mob.getMainHandItem().getItem() instanceof SpearItem;
	}

	private int getKineticWeaponUseDuration() {
		return this.mob.getMainHandItem().getItem() instanceof SpearItem spear
				? spear.computeDamageUseDuration() : 0;
	}

	@Override
	public boolean canContinueToUse() {
		return this.state != null && !this.state.done && this.ableToAttack();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void start() {
		super.start();
		this.mob.setAggressive(true);
		this.state = new SpearUseState();
	}

	@Override
	public void stop() {
		super.stop();
		this.mob.getNavigation().stop();
		this.mob.setAggressive(false);
		this.state = null;
		this.mob.stopUsingItem();
	}

	@Override
	public void tick() {
		if (this.state == null) {
			return;
		}
		LivingEntity target = this.mob.getTarget();
		if (target == null) {
			return;
		}
		double targetDistSqr = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
		int mountDistance = this.mob.isPassenger() ? 2 : 0;
		this.mob.lookAt(target, 30.0F, 30.0F);
		this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

		if (this.state.notEngagedYet()) {
			if (targetDistSqr > this.approachDistanceSq) {
				this.mob.getNavigation().moveTo(target, this.speedModifierWhenRepositioning);
				return;
			}
			this.state.startEngagement(this.getKineticWeaponUseDuration());
			this.mob.startUsingItem(InteractionHand.MAIN_HAND);
		}

		if (this.state.tickAndCheckEngagement()) {
			this.mob.stopUsingItem();
			double distance = Math.sqrt(targetDistSqr);
			this.state.awayPos = LandRandomPos.getPosAway(this.mob,
					(int) Math.max(1.0, MAX_COOLDOWN_DISTANCE + mountDistance - distance), 7, target.position());
			this.state.fleeingTime = 1;
		}

		if (!this.state.tickAndCheckFleeing()) {
			if (this.state.awayPos != null) {
				this.mob.getNavigation().moveTo(this.state.awayPos.x, this.state.awayPos.y, this.state.awayPos.z,
						this.speedModifierWhenRepositioning);
				if (this.mob.getNavigation().isDone()) {
					if (this.state.fleeingTime > 0) {
						this.state.done = true;
						return;
					}
					this.state.awayPos = null;
				}
			} else {
				this.mob.getNavigation().moveTo(target, this.speedModifierWhenCharging);
				if (targetDistSqr < this.targetInRangeRadiusSq || this.mob.getNavigation().isDone()) {
					double distance = Math.sqrt(targetDistSqr);
					this.state.awayPos = LandRandomPos.getPosAway(this.mob,
							(int) Math.max(1.0, MAX_REPOSITION_DISTANCE + mountDistance - distance), 7, target.position());
				}
			}
		}
	}

	private static class SpearUseState {
		private int engageTime = -1;
		private int fleeingTime = -1;
		@Nullable
		private Vec3 awayPos;
		private boolean done = false;

		boolean notEngagedYet() {
			return this.engageTime < 0;
		}

		void startEngagement(int spearDownTime) {
			this.engageTime = spearDownTime;
		}

		boolean tickAndCheckEngagement() {
			if (this.engageTime > 0) {
				this.engageTime--;
				return this.engageTime == 0;
			}
			return false;
		}

		boolean tickAndCheckFleeing() {
			if (this.fleeingTime > 0) {
				this.fleeingTime++;
				if (this.fleeingTime > MAX_FLEEING_TIME) {
					this.done = true;
					return true;
				}
			}
			return false;
		}
	}
}
