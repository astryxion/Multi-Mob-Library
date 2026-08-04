package net.daveyx0.multimob.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

import java.util.EnumSet;

public class EntityAICustomOwnerHurtTarget extends TargetGoal {
   TamableAnimal tameable;
   LivingEntity attacker;
   private int timestamp;

   public EntityAICustomOwnerHurtTarget(TamableAnimal theEntityTameableIn) {
      super(theEntityTameableIn, false);
      this.tameable = theEntityTameableIn;
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
            this.attacker = entitylivingbase.getLastHurtMob();
            int i = entitylivingbase.getLastHurtMobTimestamp();
            return i != this.timestamp && this.attacker != null && this.mob.canAttack(this.attacker) && this.tameable.wantsToAttack(this.attacker, entitylivingbase);
         }
      }
   }

   @Override
   public void start() {
      this.mob.setTarget(this.attacker);
      LivingEntity entitylivingbase = this.tameable.getOwner();
      if (entitylivingbase != null) {
         this.timestamp = entitylivingbase.getLastHurtMobTimestamp();
      }

      super.start();
   }
}
