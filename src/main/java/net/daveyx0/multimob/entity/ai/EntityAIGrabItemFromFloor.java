package net.daveyx0.multimob.entity.ai;

import java.util.List;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;

public class EntityAIGrabItemFromFloor extends Goal {
   private final PathfinderMob temptedEntity;
   private final double speed;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double pitch;
   private double yaw;
   private ItemEntity temptingItem;
   private boolean isRunning;
   private final Set<ItemStack> temptItem;
   private boolean canGetScared;
   private int stealDelay = 0;

   public EntityAIGrabItemFromFloor(PathfinderMob temptedEntityIn, double speedIn, Set<ItemStack> temptItemIn, boolean canGetScared) {
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

         List<ItemEntity> list = this.temptedEntity.level().getEntitiesOfClass(ItemEntity.class, this.temptedEntity.getBoundingBox().inflate(10.0D, 10.0D, 10.0D));

         for (ItemEntity item : list) {
            ItemStack stack = item.getItem();
            if (!stack.isEmpty() && this.isTempting(stack)) {
               this.temptingItem = item;
               return true;
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
      return this.canUse();
   }

   @Override
   public void start() {
      this.targetX = this.temptingItem.getX();
      this.targetY = this.temptingItem.getY();
      this.targetZ = this.temptingItem.getZ();
      this.isRunning = true;
   }

   @Override
   public void stop() {
      this.temptingItem = null;
      this.temptedEntity.getNavigation().stop();
      this.isRunning = false;
      if (this.canGetScared) {
         this.stealDelay = 50;
      }

   }

   @Override
   public void tick() {
      this.temptedEntity.getLookControl().setLookAt(this.temptingItem, (float)(this.temptedEntity.getMaxHeadXRot() + 20), (float)this.temptedEntity.getMaxHeadYRot());
      if (this.temptedEntity.distanceToSqr(this.temptingItem) < 1.0D) {
         this.temptedEntity.getNavigation().stop();
         ItemStack loot = this.temptingItem.getItem().copy();
         this.temptingItem.discard();
         this.temptedEntity.setItemSlot(EquipmentSlot.MAINHAND, loot);
      } else {
         this.temptedEntity.getNavigation().moveTo(this.temptingItem, this.speed);
      }

   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
