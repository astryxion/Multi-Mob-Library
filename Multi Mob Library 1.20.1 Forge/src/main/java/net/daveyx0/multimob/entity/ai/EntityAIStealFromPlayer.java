package net.daveyx0.multimob.entity.ai;

import java.util.Set;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;

public class EntityAIStealFromPlayer extends Goal {
   private final PathfinderMob temptedEntity;
   private final double speed;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double pitch;
   private double yaw;
   private Player temptingPlayer;
   private boolean isRunning;
   private final Set<ItemStack> temptItem;
   private boolean canGetScared;
   private int stealDelay = 0;
   private int searchCooldown = 0;

   public EntityAIStealFromPlayer(PathfinderMob temptedEntityIn, double speedIn, Set<ItemStack> temptItemIn, boolean canGetScared) {
      this.temptedEntity = temptedEntityIn;
      this.speed = speedIn;
      this.temptItem = temptItemIn;
      this.canGetScared = canGetScared;
      if (!(temptedEntityIn.getNavigation() instanceof GroundPathNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
      }
   }

   @Override
   public boolean canUse() {
      if (this.temptedEntity.getLastHurtByMob() != null && this.canGetScared && this.stealDelay <= 0) {
         this.stop();
         return false;
      } else if (!this.temptedEntity.getMainHandItem().isEmpty()) {
         return false;
      } else {
         if (this.stealDelay > 0) {
            --this.stealDelay;
            if (this.stealDelay == 0) {
               this.temptedEntity.setLastHurtByMob(null);
            }

            return false;
         }

         if (this.searchCooldown > 0) {
            --this.searchCooldown;
            return false;
         }
         this.searchCooldown = 10;

         this.temptingPlayer = this.temptedEntity.level().getNearestPlayer(this.temptedEntity, 10.0D);
         if (this.temptingPlayer != null) {
            for(int i = 0; i < this.temptingPlayer.getInventory().getContainerSize(); ++i) {
               ItemStack item = this.temptingPlayer.getInventory().getItem(i);
               if (!item.isEmpty() && this.isTempting(item)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected boolean isTempting(ItemStack stack) {
      if (!stack.isEmpty()) {
         for(ItemStack item : this.temptItem) {
            if (item != null && item.getItem() == stack.getItem() && item.getDamageValue() == stack.getDamageValue()) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean canContinueToUse() {
      if (this.temptingPlayer == null || !this.temptingPlayer.isAlive() || this.temptingPlayer.isSpectator()) {
         return false;
      }
      if (!this.temptedEntity.getMainHandItem().isEmpty()) {
         return false;
      }
      if (this.temptedEntity.getLastHurtByMob() != null && this.canGetScared) {
         return false;
      }
      return this.temptedEntity.distanceToSqr(this.temptingPlayer) <= 256.0D;
   }

   @Override
   public void start() {
      this.targetX = this.temptingPlayer.getX();
      this.targetY = this.temptingPlayer.getY();
      this.targetZ = this.temptingPlayer.getZ();
      this.isRunning = true;
   }

   @Override
   public void stop() {
      this.temptingPlayer = null;
      this.temptedEntity.getNavigation().stop();
      this.isRunning = false;
      if (this.canGetScared) {
         this.stealDelay = 100;
      }

   }

   @Override
   public void tick() {
      this.temptedEntity.getLookControl().setLookAt(this.temptingPlayer, (float)(this.temptedEntity.getMaxHeadXRot() + 20), (float)this.temptedEntity.getMaxHeadYRot());
      if (this.temptedEntity.distanceToSqr(this.temptingPlayer) < 3.25D) {
         this.temptedEntity.getNavigation().stop();

         for(int i = 0; i < this.temptingPlayer.getInventory().getContainerSize(); ++i) {
            ItemStack item = this.temptingPlayer.getInventory().getItem(i);
            if (!item.isEmpty()) {
               for(ItemStack itemstack : this.temptItem) {
                  if (itemstack != null && !itemstack.isEmpty() && itemstack.getItem() == item.getItem() && itemstack.getDamageValue() == item.getDamageValue()) {
                     Level world = this.temptingPlayer.level();
                     this.temptingPlayer.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
                     ItemStack loot = item.split(1);
                     this.temptedEntity.setItemSlot(EquipmentSlot.MAINHAND, loot);
                     if (!this.temptingPlayer.getAbilities().instabuild) {
                        item.shrink(1);
                     }

                     return;
                  }
               }
            }
         }
      } else {
         this.temptedEntity.getNavigation().moveTo(this.temptingPlayer, this.speed);
      }

   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
