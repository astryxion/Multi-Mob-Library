package net.daveyx0.multimob.entity.ai;

import net.daveyx0.multimob.common.capabilities.CapabilityTameableEntity;
import net.daveyx0.multimob.common.capabilities.ITameableEntity;
import net.daveyx0.multimob.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.util.Mth;

import java.util.EnumSet;

public class EntityAITameableFollowOwner extends Goal {
   private final Mob tameable;
   private LivingEntity owner;
   Level world;
   private final double followSpeed;
   private final PathNavigation petPathfinder;
   private int timeToRecalcPath;
   float maxDist;
   float minDist;
   private float oldWaterCost;

   public EntityAITameableFollowOwner(Mob tameable, double followSpeedIn, float minDistIn, float maxDistIn) {
      this.tameable = tameable;
      this.world = tameable.level();
      this.followSpeed = followSpeedIn;
      this.petPathfinder = tameable.getNavigation();
      this.minDist = minDistIn;
      this.maxDist = maxDistIn;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      if (!(tameable.getNavigation() instanceof GroundPathNavigation) && !(tameable.getNavigation() instanceof FlyingPathNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   @Override
   public boolean canUse() {
      if (this.tameable != null) {
         ITameableEntity tameableEntity = EntityUtil.getCapability(this.tameable, CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null);
         if (tameableEntity == null || !tameableEntity.isTamed() || tameableEntity.getFollowState() != 2) {
            return false;
         }
         LivingEntity entitylivingbase = tameableEntity.getOwner(this.tameable);
         if (entitylivingbase == null) {
            return false;
         } else if (entitylivingbase instanceof Player && ((Player)entitylivingbase).isSpectator()) {
            return false;
         } else if (this.tameable.distanceToSqr(entitylivingbase) < (double)(this.minDist * this.minDist)) {
            return false;
         } else {
            this.owner = entitylivingbase;
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean canContinueToUse() {
      ITameableEntity tameableEntity = EntityUtil.getCapability(this.tameable, CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null);
      if (tameableEntity == null || !tameableEntity.isTamed() || tameableEntity.getFollowState() != 2) {
         return false;
      }

      return !this.petPathfinder.isDone() && this.tameable.distanceToSqr(this.owner) > (double)(this.maxDist * this.maxDist);
   }

   @Override
   public void start() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.tameable.getPathfindingMalus(BlockPathTypes.WATER);
      this.tameable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
   }

   @Override
   public void stop() {
      this.owner = null;
      this.petPathfinder.stop();
      this.tameable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
   }

   @Override
   public void tick() {
      this.tameable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tameable.getMaxHeadYRot());
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = 10;
         if (!this.petPathfinder.moveTo(this.owner, this.followSpeed) && !this.tameable.isLeashed() && !this.tameable.isPassenger() && this.tameable.getTarget() == null && this.tameable.distanceToSqr(this.owner) >= 144.0D) {
            int i = Mth.floor(this.owner.getX()) - 2;
            int j = Mth.floor(this.owner.getZ()) - 2;
            int k = Mth.floor(this.owner.getBoundingBox().minY);

            for(int l = 0; l <= 4; ++l) {
               for(int i1 = 0; i1 <= 4; ++i1) {
                  if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.isTeleportFriendlyBlock(i, j, k, l, i1)) {
                     this.tameable.moveTo((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.tameable.getYRot(), this.tameable.getXRot());
                     this.petPathfinder.stop();
                     return;
                  }
               }
            }
         }
      }

   }

   protected boolean isTeleportFriendlyBlock(int x, int p_192381_2_, int y, int p_192381_4_, int p_192381_5_) {
      BlockPos blockpos = new BlockPos(x + p_192381_4_, y - 1, p_192381_2_ + p_192381_5_);
      BlockState iblockstate = this.world.getBlockState(blockpos);
      return iblockstate.entityCanStandOnFace(this.world, blockpos, this.tameable, Direction.UP) && this.world.isEmptyBlock(blockpos.above()) && this.world.isEmptyBlock(blockpos.above(2));
   }
}
