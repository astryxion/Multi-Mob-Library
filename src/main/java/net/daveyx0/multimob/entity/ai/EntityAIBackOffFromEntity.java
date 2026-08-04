package net.daveyx0.multimob.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * Soft spacing goal: nudges the mob away from its target when too close.
 * Does not claim MOVE (so casting / other goals can run), and does not path-flee.
 */
public class EntityAIBackOffFromEntity extends Goal {
   PathfinderMob creature;
   LivingEntity target;
   double maxDistance;
   boolean defensiveAttack;

   public EntityAIBackOffFromEntity(PathfinderMob entitycreature, double maxDistance, boolean defensiveAttack) {
      this.creature = entitycreature;
      this.maxDistance = maxDistance;
      this.defensiveAttack = defensiveAttack;
   }

   @Override
   public boolean canUse() {
      this.target = this.creature.getTarget();
      if (this.target == null) {
         return false;
      } else if (!this.target.isAlive()) {
         return false;
      } else {
         return (double)this.creature.distanceTo(this.target) <= this.maxDistance && this.creature.hasLineOfSight(this.target);
      }
   }

   @Override
   public boolean canContinueToUse() {
      return this.canUse();
   }

   @Override
   public void stop() {
      this.target = null;
   }

   @Override
   public void tick() {
      if (this.target != null) {
         this.creature.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

         // Soft shove away from target (original Multi Mob behavior) — no pathfinding kite
         double dx = this.creature.getX() - this.target.getX();
         double dz = this.creature.getZ() - this.target.getZ();
         double dist = Math.sqrt(dx * dx + dz * dz);
         if (dist > 1.0E-4D) {
            Vec3 motion = this.creature.getDeltaMovement();
            double push = 0.15D;
            this.creature.setDeltaMovement(motion.x + (dx / dist) * push, motion.y, motion.z + (dz / dist) * push);
         }

         if (this.defensiveAttack && this.creature instanceof Creeper && !this.creature.level().isClientSide && this.creature.getHealth() < this.creature.getMaxHealth() / 2.0F && this.creature.getRandom().nextInt(20) == 0) {
            int i = (int)(this.creature.getX() - 0.5F);
            int j = (int)this.creature.getY();
            int k = (int)(this.creature.getZ() - 0.5F);
            BlockPos pos = new BlockPos(i, j, k);
            Block block = this.creature.level().getBlockState(pos).getBlock();
            if (this.creature.level().getBlockState(pos).canBeReplaced() || block == Blocks.AIR) {
               this.creature.level().setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
            }
         }
      }
   }
}
