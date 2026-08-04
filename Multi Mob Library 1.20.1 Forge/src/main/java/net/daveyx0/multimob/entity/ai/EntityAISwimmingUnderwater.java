package net.daveyx0.multimob.entity.ai;

import net.daveyx0.multimob.entity.EntityMMSwimmingCreature;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class EntityAISwimmingUnderwater extends Goal {
   private EntityMMSwimmingCreature swimmingEntity;
   private double xPosition;
   private double yPosition;
   private double zPosition;

   public EntityAISwimmingUnderwater(EntityMMSwimmingCreature entity) {
      this.swimmingEntity = entity;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   @Override
   public boolean canUse() {
      if (this.swimmingEntity.isInWater() && !this.swimmingEntity.isInFlowingWater()) {
         Vec3 vec3d = DefaultRandomPos.getPos(this.swimmingEntity, 6, 2);

         if (vec3d == null) {
            return false;
         } else {
            this.xPosition = vec3d.x;
            this.yPosition = vec3d.y;
            this.zPosition = vec3d.z;
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void stop() {
      this.xPosition = 0.0D;
      this.yPosition = 0.0D;
      this.zPosition = 0.0D;
   }

   @Override
   public boolean canContinueToUse() {
      if (this.swimmingEntity.isInWater() && !this.swimmingEntity.isInFlowingWater()) {
         return !this.swimmingEntity.getNavigation().isDone();
      } else {
         return false;
      }
   }

   @Override
   public void start() {
      if (this.xPosition != 0.0D && this.yPosition != 0.0D && this.zPosition != 0.0D) {
         this.swimmingEntity.getNavigation().moveTo(this.xPosition, this.yPosition, this.zPosition, 1.0D);
      }

   }
}
