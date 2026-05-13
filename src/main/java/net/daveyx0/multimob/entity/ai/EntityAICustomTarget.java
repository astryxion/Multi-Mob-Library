package net.daveyx0.multimob.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.scores.Team;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;

public abstract class EntityAICustomTarget extends Goal {
   protected final Mob taskOwner;
   protected boolean shouldCheckSight;
   private final boolean nearbyOnly;
   private int targetSearchStatus;
   private int targetSearchDelay;
   private int targetUnseenTicks;
   protected LivingEntity target;
   protected int unseenMemoryTicks;

   public EntityAICustomTarget(Mob creature, boolean checkSight) {
      this(creature, checkSight, false);
   }

   public EntityAICustomTarget(Mob creature, boolean checkSight, boolean onlyNearby) {
      this.unseenMemoryTicks = 60;
      this.taskOwner = creature;
      this.shouldCheckSight = checkSight;
      this.nearbyOnly = onlyNearby;
   }

   @Override
   public boolean canContinueToUse() {
      LivingEntity entitylivingbase = this.taskOwner.getTarget();
      if (entitylivingbase == null) {
         entitylivingbase = this.target;
      }

      if (entitylivingbase == null) {
         return false;
      } else if (!entitylivingbase.isAlive()) {
         return false;
      } else {
         Team team = this.taskOwner.getTeam();
         Team team1 = entitylivingbase.getTeam();
         if (team != null && team1 == team) {
            return false;
         } else {
            double d0 = this.getTargetDistance();
            if (this.taskOwner.distanceToSqr(entitylivingbase) > d0 * d0) {
               return false;
            } else {
               if (this.shouldCheckSight) {
                  if (this.taskOwner.getSensing().hasLineOfSight(entitylivingbase)) {
                     this.targetUnseenTicks = 0;
                  } else if (++this.targetUnseenTicks > this.unseenMemoryTicks) {
                     return false;
                  }
               }

               if (entitylivingbase instanceof Player && ((Player)entitylivingbase).getAbilities().invulnerable) {
                  return false;
               } else {
                  this.taskOwner.setTarget(entitylivingbase);
                  return true;
               }
            }
         }
      }
   }

   protected double getTargetDistance() {
      AttributeInstance iattributeinstance = this.taskOwner.getAttribute(Attributes.FOLLOW_RANGE);
      return iattributeinstance == null ? 16.0D : iattributeinstance.getValue();
   }

   @Override
   public void start() {
      this.targetSearchStatus = 0;
      this.targetSearchDelay = 0;
      this.targetUnseenTicks = 0;
   }

   @Override
   public void stop() {
      this.taskOwner.setTarget((LivingEntity)null);
      this.target = null;
   }

   public static boolean isSuitableTarget(Mob attacker, @Nullable LivingEntity target, boolean includeInvincibles, boolean checkSight) {
      if (target == null) {
         return false;
      } else if (target == attacker) {
         return false;
      } else if (!target.isAlive()) {
         return false;
      } else if (!attacker.canAttack(target)) {
         return false;
      } else if (attacker.isAlliedTo(target)) {
         return false;
      } else {
         if (attacker instanceof OwnableEntity && ((OwnableEntity)attacker).getOwnerUUID() != null) {
            if (target instanceof OwnableEntity && ((OwnableEntity)attacker).getOwnerUUID().equals(((OwnableEntity)target).getOwnerUUID())) {
               return false;
            }

            if (target == ((OwnableEntity)attacker).getOwner()) {
               return false;
            }
         } else if (target instanceof Player && !includeInvincibles && ((Player)target).getAbilities().invulnerable) {
            return false;
         }

         return !checkSight || attacker.getSensing().hasLineOfSight(target);
      }
   }

   protected boolean isSuitableTarget(@Nullable LivingEntity target, boolean includeInvincibles) {
      if (!isSuitableTarget(this.taskOwner, target, includeInvincibles, this.shouldCheckSight)) {
         return false;
      } else {
         if (this.nearbyOnly) {
            if (--this.targetSearchDelay <= 0) {
               this.targetSearchStatus = 0;
            }

            if (this.targetSearchStatus == 0) {
               this.targetSearchStatus = this.canEasilyReach(target) ? 1 : 2;
            }

            if (this.targetSearchStatus == 2) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean canEasilyReach(LivingEntity target) {
      this.targetSearchDelay = 10 + this.taskOwner.getRandom().nextInt(5);
      Path path = this.taskOwner.getNavigation().createPath(target, 0);
      if (path == null) {
         return false;
      } else {
         Node pathpoint = path.getEndNode();
         if (pathpoint == null) {
            return false;
         } else {
            int i = pathpoint.x - Mth.floor(target.getX());
            int j = pathpoint.z - Mth.floor(target.getZ());
            return (double)(i * i + j * j) <= 2.25D;
         }
      }
   }

   public EntityAICustomTarget setUnseenMemoryTicks(int p_190882_1_) {
      this.unseenMemoryTicks = p_190882_1_;
      return this;
   }
}
