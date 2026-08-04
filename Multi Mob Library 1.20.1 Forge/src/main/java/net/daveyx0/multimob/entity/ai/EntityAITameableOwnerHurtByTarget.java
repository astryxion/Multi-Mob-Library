package net.daveyx0.multimob.entity.ai;

import net.daveyx0.multimob.common.capabilities.CapabilityTameableEntity;
import net.daveyx0.multimob.common.capabilities.ITameableEntity;
import net.daveyx0.multimob.util.EntityUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.EnumSet;

public class EntityAITameableOwnerHurtByTarget extends EntityAICustomTarget {
   Mob tamed;
   LivingEntity attacker;
   private int timestamp;

   public EntityAITameableOwnerHurtByTarget(Mob tameableEntity) {
      super(tameableEntity, false);
      this.tamed = tameableEntity;
      this.setFlags(EnumSet.of(Flag.TARGET));
   }

   @Override
   public boolean canUse() {
      ITameableEntity tameable = EntityUtil.getCapability(this.tamed, CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null);
      if (tameable == null) {
         return false;
      } else {
         LivingEntity entitylivingbase = tameable.getOwner(this.tamed);
         if (entitylivingbase == null) {
            return false;
         } else {
            this.attacker = entitylivingbase.getLastHurtByMob();
            int i = entitylivingbase.getLastHurtByMobTimestamp();
            return i != this.timestamp && this.isSuitableTarget(this.attacker, false);
         }
      }
   }

   @Override
   public void start() {
      this.taskOwner.setTarget(this.attacker);
      ITameableEntity tameable = EntityUtil.getCapability(this.tamed, CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null);
      LivingEntity entitylivingbase = tameable.getOwner(this.tamed);
      if (entitylivingbase != null) {
         this.timestamp = entitylivingbase.getLastHurtByMobTimestamp();
      }

      super.start();
   }
}
