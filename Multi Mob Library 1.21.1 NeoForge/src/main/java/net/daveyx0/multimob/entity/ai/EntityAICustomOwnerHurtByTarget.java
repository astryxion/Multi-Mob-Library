package net.daveyx0.multimob.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

import java.util.EnumSet;

public class EntityAICustomOwnerHurtByTarget extends TargetGoal {
   TamableAnimal tameable;
   LivingEntity attacker;
   private int timestamp;

   public EntityAICustomOwnerHurtByTarget(TamableAnimal theDefendingTameableIn) {
      super(theDefendingTameableIn, false);
      this.tameable = theDefendingTameableIn;
      this.setFlags(EnumSet.of(Flag.TARGET));
   }

   @Override
   public boolean canUse() {
      if (!this.tameable.isTame()) {
         return false;
      } else {
         LivingEntity entitylivingbase = this.tameable.getOwner();
         if (entitylivingbase == null) {
            return false;
         } else {
            this.attacker = entitylivingbase.getLastHurtByMob();
            int i = entitylivingbase.getLastHurtByMobTimestamp();
            return i != this.timestamp && this.attacker != null && this.mob.canAttack(this.attacker) && this.tameable.wantsToAttack(this.attacker, entitylivingbase);
         }
      }
   }

   @Override
   public void start() {
      this.mob.setTarget(this.attacker);
      LivingEntity entitylivingbase = this.tameable.getOwner();
      if (entitylivingbase != null) {
         this.timestamp = entitylivingbase.getLastHurtByMobTimestamp();
      }

      super.start();
   }
}
