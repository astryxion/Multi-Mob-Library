package net.daveyx0.multimob.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * Minimal 1.21 shell. Full flying monster helpers from 1.20.1 stubbed for mapping remaps.
 */
public class EntityMMFlyingMob extends Monster implements FlyingAnimal {

   public EntityMMFlyingMob(EntityType<? extends EntityMMFlyingMob> type, Level level) {
      super(type, level);
      this.moveControl = new FlyingMoveControl(this, 10, false);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FLYING_SPEED, 0.4D)
         .add(Attributes.MAX_HEALTH, 6.0D)
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
