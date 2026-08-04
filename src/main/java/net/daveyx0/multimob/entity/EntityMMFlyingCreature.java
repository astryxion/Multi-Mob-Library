package net.daveyx0.multimob.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.Level;

/**
 * Minimal 1.21 shell. Full flying combat helpers from 1.20.1 were stubbed due to enchantment API remaps.
 */
public class EntityMMFlyingCreature extends PathfinderMob implements FlyingAnimal {

   public EntityMMFlyingCreature(EntityType<? extends EntityMMFlyingCreature> type, Level level) {
      super(type, level);
   }

   @Override
   public void tick() {
      super.tick();
      this.fallDistance = 0.0F;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return PathfinderMob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 10.0D)
         .add(Attributes.FLYING_SPEED, 0.4D)
         .add(Attributes.MOVEMENT_SPEED, 0.2D);
   }

   @Override
   public boolean isFlying() {
      return !this.onGround();
   }

   @Override
   protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos) {
   }
}
