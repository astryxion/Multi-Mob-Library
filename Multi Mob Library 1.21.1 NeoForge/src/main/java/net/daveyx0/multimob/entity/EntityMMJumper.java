package net.daveyx0.multimob.entity;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

/**
 * Minimal 1.21 shell. Full jumper AI from 1.20.1 stubbed (jumpFromGround visibility / breed offspring).
 */
public class EntityMMJumper extends Animal {
   public int maxJumpDuration = 10;
   public float jumpMultiplier = 1.0F;

   public EntityMMJumper(EntityType<? extends EntityMMJumper> type, Level level) {
      super(type, level);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 8.0D)
         .add(Attributes.MOVEMENT_SPEED, 0.2D);
   }

   @Nullable
   @Override
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
      return null;
   }

   @Override
   public boolean isFood(net.minecraft.world.item.ItemStack stack) {
      return false;
   }

   public void setMovementSpeed(double newSpeed) {
      this.getNavigation().setSpeedModifier(newSpeed);
   }
}
