package net.daveyx0.multimob.entity.ai;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;

public class EntityAITemptItemStack extends Goal {
   public final PathfinderMob temptedEntity;
   private final double speed;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double pitch;
   private double yaw;
   private Player temptingPlayer;
   private int delayTemptCounter;
   private boolean isRunning;
   private final Set<ItemStack> temptItem;
   private final boolean scaredByPlayerMovement;

   public EntityAITemptItemStack(PathfinderMob temptedEntityIn, double speedIn, ItemStack temptItemIn, boolean scaredByPlayerMovementIn) {
      this(temptedEntityIn, speedIn, scaredByPlayerMovementIn, Sets.newHashSet(temptItemIn));
   }

   public EntityAITemptItemStack(PathfinderMob temptedEntityIn, double speedIn, boolean scaredByPlayerMovementIn, Set<ItemStack> temptItemIn) {
      this.temptedEntity = temptedEntityIn;
      this.speed = speedIn;
      this.temptItem = temptItemIn;
      this.scaredByPlayerMovement = scaredByPlayerMovementIn;
      if (!(temptedEntityIn.getNavigation() instanceof GroundPathNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
      }
   }

   @Override
   public boolean canUse() {
      if (this.delayTemptCounter > 0) {
         --this.delayTemptCounter;
         return false;
      } else {
         this.temptingPlayer = this.temptedEntity.level().getNearestPlayer(this.temptedEntity, 10.0D);
         return this.temptingPlayer == null ? false : this.isTempting(this.temptingPlayer.getMainHandItem()) || this.isTempting(this.temptingPlayer.getOffhandItem());
      }
   }

   protected boolean isTempting(ItemStack stack) {
      if (!stack.isEmpty()) {
         for(ItemStack item : this.temptItem) {
            if (item != null && item.getItem() == stack.getItem() && item.getDamageValue() == stack.getDamageValue()) {
               return true;
            }
         }

         ItemStack heldItem = this.temptedEntity.getMainHandItem();
         if (!heldItem.isEmpty() && heldItem.getItem() == stack.getItem() && heldItem.getDamageValue() == stack.getDamageValue()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean canContinueToUse() {
      if (this.scaredByPlayerMovement) {
         if (this.temptedEntity.distanceToSqr(this.temptingPlayer) < 36.0D) {
            if (this.temptingPlayer.distanceToSqr(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002D) {
               return false;
            }

            if (Math.abs((double)this.temptingPlayer.getXRot() - this.pitch) > 5.0D || Math.abs((double)this.temptingPlayer.getYRot() - this.yaw) > 5.0D) {
               return false;
            }
         } else {
            this.targetX = this.temptingPlayer.getX();
            this.targetY = this.temptingPlayer.getY();
            this.targetZ = this.temptingPlayer.getZ();
         }

         this.pitch = (double)this.temptingPlayer.getXRot();
         this.yaw = (double)this.temptingPlayer.getYRot();
      }

      return this.canUse();
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
      this.delayTemptCounter = 25;
      this.isRunning = false;
   }

   @Override
   public void tick() {
      this.temptedEntity.getLookControl().setLookAt(this.temptingPlayer, (float)(this.temptedEntity.getMaxHeadXRot() + 20), (float)this.temptedEntity.getMaxHeadYRot());
      if (this.temptedEntity.distanceToSqr(this.temptingPlayer) < 6.25D) {
         this.temptedEntity.getNavigation().stop();
      } else {
         this.temptedEntity.getNavigation().moveTo(this.temptingPlayer, this.speed);
      }

   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
