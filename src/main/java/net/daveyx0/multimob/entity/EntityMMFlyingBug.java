package net.daveyx0.multimob.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntityMMFlyingBug extends AmbientCreature implements FlyingAnimal {
   public EntityMMFlyingBug(EntityType<? extends EntityMMFlyingBug> type, Level worldIn) {
      super(type, worldIn);
   }

   @Override
   public boolean hurt(DamageSource source, float amount) {
      return this.isInvulnerableTo(source) ? false : super.hurt(source, amount);
   }

   @Override
   public boolean isPushable() {
      return false;
   }

   @Override
   protected void doPush(Entity entityIn) {
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   @Override
   public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
      return false;
   }

   @Override
   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   @Override
   public boolean isIgnoringBlockTriggers() {
      return true;
   }

   @Override
   public boolean isFlying() {
      return !this.onGround();
   }
}
