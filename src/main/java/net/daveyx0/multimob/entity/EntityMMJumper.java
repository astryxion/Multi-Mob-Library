package net.daveyx0.multimob.entity;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityMMJumper extends Animal {
   public int maxJumpDuration = 10;
   private int jumpTicks;
   private int jumpDuration;
   private boolean wasOnGround;
   private int currentMoveTypeDuration;
   public float jumpMultiplier = 1.0F;

   public EntityMMJumper(EntityType<? extends EntityMMJumper> type, Level level) {
      super(type, level);
      this.jumpControl = new JumperJumpHelper(this);
      this.moveControl = new JumperMoveHelper(this);
      this.setMovementSpeed(0.0D);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 8.0D)
         .add(Attributes.MOVEMENT_SPEED, 0.2D);
   }

   @Override
   protected float getJumpPower() {
      if (!this.horizontalCollision && (!this.moveControl.hasWanted() || this.moveControl.getWantedY() <= this.getY() + 0.5D)) {
         Path path = this.getNavigation().getPath();
         if (path != null && path.getNextNodeIndex() < path.getNodeCount()) {
            Vec3 vec3 = path.getNextEntityPos(this);
            if (vec3.y > this.getY() + 0.5D) {
               return 0.5F * this.jumpMultiplier;
            }
         }
         return this.moveControl.getSpeedModifier() <= 0.6D ? 0.2F * this.jumpMultiplier : 0.3F * this.jumpMultiplier;
      } else {
         return 0.5F;
      }
   }

   @Override
   public void jumpFromGround() {
      super.jumpFromGround();
      double d0 = this.moveControl.getSpeedModifier();
      if (d0 > 0.0D) {
         double d1 = this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z;
         if (d1 < 0.010000000000000002D) {
            this.moveRelative(0.1F, new Vec3(0.0, 0.0, 1.0));
         }
      }
      if (!this.level().isClientSide) {
         this.level().broadcastEntityEvent(this, (byte)1);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public float setJumpCompletion(float partialTick) {
      return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + partialTick) / (float)this.jumpDuration;
   }

   public void setMovementSpeed(double newSpeed) {
      this.getNavigation().setSpeedModifier(newSpeed);
      this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), newSpeed);
   }

   @Override
   public void setJumping(boolean jumping) {
      super.setJumping(jumping);
      if (jumping) {
         this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }
   }

   public void startJumping() {
      this.setJumping(true);
      this.jumpDuration = this.maxJumpDuration;
      this.jumpTicks = 0;
   }

   @Override
   protected void customServerAiStep() {
      if (this.currentMoveTypeDuration > 0) {
         --this.currentMoveTypeDuration;
      }

      if (this.onGround() && !this.isInWater()) {
         if (!this.wasOnGround) {
            this.setJumping(false);
            this.checkLandingDelay();
         }

         JumperJumpHelper jumperjumphelper = (JumperJumpHelper)this.jumpControl;
         if (!jumperjumphelper.getIsJumping()) {
            if (this.moveControl.hasWanted() && this.currentMoveTypeDuration == 0) {
               Path path = this.getNavigation().getPath();
               Vec3 vec3 = new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
               if (path != null && path.getNextNodeIndex() < path.getNodeCount()) {
                  vec3 = path.getNextEntityPos(this);
               }
               this.calculateRotationYaw(vec3.x, vec3.z);
               this.startJumping();
            }
         } else if (!jumperjumphelper.canJump()) {
            this.enableJumpControl();
         }
      }

      this.wasOnGround = this.onGround();
   }

   @Override
   public void spawnSprintParticle() {
   }

   private void calculateRotationYaw(double x, double z) {
      this.setYRot((float)(Mth.atan2(z - this.getZ(), x - this.getX()) * (180D / Math.PI)) - 90.0F);
   }

   private void enableJumpControl() {
      ((JumperJumpHelper)this.jumpControl).setCanJump(true);
   }

   private void disableJumpControl() {
      ((JumperJumpHelper)this.jumpControl).setCanJump(false);
   }

   private void updateMoveTypeDuration() {
      if (this.moveControl.getSpeedModifier() < 2.2D) {
         this.currentMoveTypeDuration = 10;
      } else {
         this.currentMoveTypeDuration = 1;
      }
   }

   private void checkLandingDelay() {
      this.updateMoveTypeDuration();
      this.disableJumpControl();
   }

   @Override
   public void aiStep() {
      super.aiStep();
      if (this.jumpTicks != this.jumpDuration) {
         ++this.jumpTicks;
      } else if (this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
         this.setJumping(false);
      }
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.RABBIT_JUMP;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void handleEntityEvent(byte id) {
      if (id == 1) {
         this.spawnSprintParticle();
         this.jumpDuration = this.maxJumpDuration;
         this.jumpTicks = 0;
      } else {
         super.handleEntityEvent(id);
      }
   }

   @Nullable
   @Override
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
      return null;
   }

   @Override
   public boolean isFood(ItemStack stack) {
      return false;
   }

   public static class AIPanic extends PanicGoal {
      private final EntityMMJumper jumper;

      public AIPanic(EntityMMJumper jumper, double speedIn) {
         super(jumper, speedIn);
         this.jumper = jumper;
      }

      @Override
      public void tick() {
         super.tick();
         this.jumper.setMovementSpeed(this.speedModifier);
      }
   }

   public static class JumperJumpHelper extends JumpControl {
      private final EntityMMJumper jumper;
      private boolean canJump;

      public JumperJumpHelper(EntityMMJumper jumper) {
         super(jumper);
         this.jumper = jumper;
      }

      public boolean getIsJumping() {
         return this.jump;
      }

      public boolean canJump() {
         return this.canJump;
      }

      public void setCanJump(boolean canJumpIn) {
         this.canJump = canJumpIn;
      }

      @Override
      public void tick() {
         if (this.jump) {
            this.jumper.startJumping();
            this.jump = false;
         }
      }
   }

   public static class JumperMoveHelper extends MoveControl {
      private final EntityMMJumper jumper;
      protected double nextJumpSpeed;

      public JumperMoveHelper(EntityMMJumper jumper) {
         super(jumper);
         this.jumper = jumper;
      }

      @Override
      public void tick() {
         if (this.jumper.onGround() && !this.jumper.jumping && !((JumperJumpHelper)this.jumper.jumpControl).getIsJumping()) {
            this.jumper.setMovementSpeed(0.0D);
         } else if (this.hasWanted()) {
            this.jumper.setMovementSpeed(this.nextJumpSpeed);
         }
         super.tick();
      }

      @Override
      public void setWantedPosition(double x, double y, double z, double speedIn) {
         if (this.jumper.isInWater()) {
            speedIn = 1.5D;
         }
         super.setWantedPosition(x, y, z, speedIn);
         if (speedIn > 0.0D) {
            this.nextJumpSpeed = speedIn;
         }
      }
   }
}
