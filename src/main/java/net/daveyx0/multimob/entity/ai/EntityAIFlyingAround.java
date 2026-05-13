package net.daveyx0.multimob.entity.ai;

import net.daveyx0.multimob.entity.EntityMMFlyingCreature;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class EntityAIFlyingAround extends Goal {
   private EntityMMFlyingCreature flyingEntity;
   private double xPosition;
   private double yPosition;
   private double zPosition;

   public EntityAIFlyingAround(EntityMMFlyingCreature entity) {
      this.flyingEntity = entity;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   @Override
   public boolean canUse() {
      if (this.flyingEntity.isInWater()) {
         return false;
      } else {
         Vec3 vec3d = DefaultRandomPos.getPos(this.flyingEntity, 6, 4);
         if (vec3d == null) {
            return false;
         } else {
            this.xPosition = vec3d.x;
            this.yPosition = vec3d.y;
            this.zPosition = vec3d.z;
            return true;
         }
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
      return !this.flyingEntity.getNavigation().isDone();
   }

   @Override
   public void start() {
      if (this.xPosition != 0.0D && this.yPosition != 0.0D && this.zPosition != 0.0D) {
         this.flyingEntity.getNavigation().moveTo(this.xPosition, this.yPosition, this.zPosition, 1.0D);
      }

   }
}
