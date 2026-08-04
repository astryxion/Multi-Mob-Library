package net.daveyx0.multimob.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class EntityMMSwimmingCreature extends PathfinderMob {
   public EntityMMSwimmingCreature(EntityType<? extends EntityMMSwimmingCreature> type, Level level) {
      super(type, level);
      this.moveControl = new SwimmingMoveHelper();
   }

   @Override
   public boolean hurt(DamageSource source, float amount) {
      return this.isInvulnerableTo(source) ? false : super.hurt(source, amount);
   }

   @Override
   public boolean doHurtTarget(Entity entityIn) {
      float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
      int i = 0;
      if (entityIn instanceof LivingEntity) {
         f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)entityIn).getMobType());
         i += EnchantmentHelper.getKnockbackBonus(this);
      }

      boolean flag = entityIn.hurt(this.damageSources().mobAttack(this), f);
      if (flag) {
         if (i > 0 && entityIn instanceof LivingEntity) {
            ((LivingEntity)entityIn).knockback((float)i * 0.5F, (double)Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
            Vec3 delta = this.getDeltaMovement();
            this.setDeltaMovement(delta.x * 0.6D, delta.y, delta.z * 0.6D);
         }

         int j = EnchantmentHelper.getFireAspect(this);
         if (j > 0) {
            entityIn.setSecondsOnFire(j * 4);
         }

         if (entityIn instanceof Player) {
            Player entityplayer = (Player)entityIn;
            ItemStack itemstack = this.getMainHandItem();
            ItemStack itemstack1 = entityplayer.isUsingItem() ? entityplayer.getUseItem() : ItemStack.EMPTY;
            if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem() instanceof AxeItem && itemstack1.getItem() instanceof ShieldItem) {
               float f1 = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
               if (this.getRandom().nextFloat() < f1) {
                  entityplayer.getCooldowns().addCooldown(Items.SHIELD, 100);
                  this.level().broadcastEntityEvent(entityplayer, (byte)30);
               }
            }
         }

         this.doEnchantDamageEffects(this, entityIn);
      }

      return flag;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return PathfinderMob.createMobAttributes()
         .add(Attributes.ATTACK_DAMAGE);
   }

   @Override
   protected PathNavigation createNavigation(Level level) {
      return new WaterBoundPathNavigation(this, level);
   }

   public boolean isInFlowingWater() {
      BlockPos pos = this.blockPosition();
      FluidState fluidState = this.level().getFluidState(pos);
      if (!fluidState.isEmpty()) {
         if ((double)fluidState.getHeight(this.level(), pos) < 0.8) {
            return true;
         }
      }
      return false;
   }

   public class SwimmingMoveHelper extends MoveControl {
      private EntityMMSwimmingCreature swimmingEntity = EntityMMSwimmingCreature.this;

      public SwimmingMoveHelper() {
         super(EntityMMSwimmingCreature.this);
      }

      @Override
      public void tick() {
         if (!this.swimmingEntity.isInWater() || this.swimmingEntity.isInFlowingWater()) {
            this.speedModifier = 0.0;
         }

         if (this.operation == MoveControl.Operation.MOVE_TO && !this.swimmingEntity.getNavigation().isDone()) {
            double d0 = this.wantedX - this.swimmingEntity.getX();
            double d1 = this.wantedY - this.swimmingEntity.getY() + 0.15D;
            double d2 = this.wantedZ - this.swimmingEntity.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)Mth.sqrt((float)d3);
            d1 /= d3;
            float f = (float)(Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
            this.swimmingEntity.setYRot(this.rotlerp(this.swimmingEntity.getYRot(), f, 30.0F));
            this.swimmingEntity.setSpeed((float)(this.speedModifier * this.swimmingEntity.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            Vec3 delta = this.swimmingEntity.getDeltaMovement();
            this.swimmingEntity.setDeltaMovement(delta.x, delta.y + (double)this.swimmingEntity.getSpeed() * d1 * 0.1, delta.z);
         } else {
            this.swimmingEntity.setSpeed(0.0F);
         }
      }
   }
}
