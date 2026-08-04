package net.daveyx0.multimob.entity;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.level.Level;

/**
 * Minimal 1.21 shell. Full parrot-like bird logic from 1.20.1 stubbed for mapping remaps
 * (finalizeSpawn / synched data / loot table ResourceKey changes).
 */
public class EntityMMBird extends ShoulderRidingEntity implements FlyingAnimal {

   public EntityMMBird(EntityType<? extends EntityMMBird> type, Level level) {
      super(type, level);
      this.moveControl = new FlyingMoveControl(this, 10, false);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 6.0D)
         .add(Attributes.FLYING_SPEED, 0.4D)
         .add(Attributes.MOVEMENT_SPEED, 0.2D);
   }

   @Override
   public boolean isFlying() {
      return !this.onGround();
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
}
