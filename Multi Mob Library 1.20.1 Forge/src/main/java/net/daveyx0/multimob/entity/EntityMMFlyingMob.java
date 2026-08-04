package net.daveyx0.multimob.entity;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class EntityMMFlyingMob extends Monster implements FlyingAnimal {
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityMMBird.class, EntityDataSerializers.INT);
   private static final Item DEADLY_ITEM = Items.COOKIE;
   private static final Set<Item> TAME_ITEMS = Sets.newHashSet(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
   public float flap;
   public float flapSpeed;
   public float oFlapSpeed;
   public float oFlap;
   public float flapping = 1.0F;

   public EntityMMFlyingMob(EntityType<? extends EntityMMFlyingMob> type, Level level) {
      super(type, level);
      this.moveControl = new FlyingMoveControl(this, 10, false);
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
      this.setVariant(this.getRandom().nextInt(2));
      return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FLYING_SPEED, 0.4D)
         .add(Attributes.MAX_HEALTH, 6.0D)
         .add(Attributes.MOVEMENT_SPEED, 0.2D);
   }

   @Override
   protected PathNavigation createNavigation(Level level) {
      FlyingPathNavigation pathnavigateflying = new FlyingPathNavigation(this, level);
      pathnavigateflying.setCanOpenDoors(false);
      pathnavigateflying.setCanFloat(true);
      pathnavigateflying.setCanPassDoors(true);
      return pathnavigateflying;
   }

   @Override
   protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
      return size.height * 0.6F;
   }

   @Override
   public void aiStep() {
      super.aiStep();
      this.calculateFlapping();
   }

   private void calculateFlapping() {
      this.oFlap = this.flap;
      this.oFlapSpeed = this.flapSpeed;
      this.flapSpeed = (float)((double)this.flapSpeed + (double)(this.onGround() ? -1 : 6) * 0.3D);
      this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
      if (!this.onGround() && this.flapping < 1.0F) {
         this.flapping = 1.0F;
      }
      this.flapping = (float)((double)this.flapping * 0.9D);
      this.flap += this.flapping * 2.0F;
   }

   @Override
   public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnType) {
      int i = Mth.floor(this.getX());
      int j = Mth.floor(this.getBoundingBox().minY);
      int k = Mth.floor(this.getZ());
      BlockPos blockpos = new BlockPos(i, j, k);
      BlockState blockstate = level.getBlockState(blockpos.below());
      Block block = blockstate.getBlock();
      return block instanceof LeavesBlock || block == Blocks.GRASS_BLOCK || blockstate.is(BlockTags.LOGS) || block == Blocks.AIR && level.getMaxLocalRawBrightness(blockpos) > 8 && super.checkSpawnRules(level, spawnType);
   }

   @Override
   public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
      return false;
   }

   @Override
   protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
   }

   public boolean canMateWith(Animal otherAnimal) {
      return false;
   }

   @Nullable
   public AgeableMob createChild(AgeableMob ageable) {
      return null;
   }

   @Override
   public boolean doHurtTarget(Entity target) {
      return target.hurt(this.damageSources().mobAttack(this), 3.0F);
   }

   @Nullable
   @Override
   public SoundEvent getAmbientSound() {
      return getAmbientSound(this.getRandom());
   }

   private static SoundEvent getAmbientSound(RandomSource random) {
      return SoundEvents.PARROT_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.PARROT_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.PARROT_DEATH;
   }

   @Override
   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.PARROT_STEP, 0.15F, 1.0F);
   }

   @Override
   protected float nextStep() {
      this.playSound(SoundEvents.PARROT_FLY, 0.15F, 1.0F);
      return this.moveDist + this.flapSpeed / 2.0F;
   }

   @Override
   public boolean isFlapping() {
      return true;
   }

   @Override
   public float getVoicePitch() {
      return getPitch(this.getRandom());
   }

   private static float getPitch(RandomSource random) {
      return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
   }

   @Override
   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   @Override
   public boolean isPushable() {
      return true;
   }

   @Override
   protected void doPush(Entity entity) {
      if (!(entity instanceof Player)) {
         super.doPush(entity);
      }
   }

   public int getVariant() {
      return Mth.clamp(this.entityData.get(VARIANT), 0, 4);
   }

   public void setVariant(int variant) {
      this.entityData.set(VARIANT, variant);
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(VARIANT, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getVariant());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setVariant(compound.getInt("Variant"));
   }

   @Override
   protected ResourceLocation getDefaultLootTable() {
      return new ResourceLocation("minecraft", "entities/parrot");
   }

   @Override
   public boolean isFlying() {
      return !this.onGround();
   }
}
