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
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntityMMFlyingCreature extends PathfinderMob implements FlyingAnimal {

	public EntityMMFlyingCreature(EntityType<? extends EntityMMFlyingCreature> type, Level level) {
		super(type, level);
		this.moveControl = new FlyingMoveHelper();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return this.isInvulnerableTo(source) ? false : super.hurt(source, amount);
	}

	@Override
	public boolean doHurtTarget(Entity entityIn) {
		float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
		int i = 0;

		if (entityIn instanceof LivingEntity) {
			f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity) entityIn).getMobType());
			i += EnchantmentHelper.getKnockbackBonus(this);
		}

		boolean flag = entityIn.hurt(this.damageSources().mobAttack(this), f);

		if (flag) {
			if (i > 0 && entityIn instanceof LivingEntity) {
				((LivingEntity) entityIn).knockback((float) i * 0.5F, (double) Mth.sin(this.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(this.getYRot() * ((float) Math.PI / 180F))));
				Vec3 motion = this.getDeltaMovement();
				this.setDeltaMovement(motion.x * 0.6D, motion.y, motion.z * 0.6D);
			}

			int j = EnchantmentHelper.getFireAspect(this);

			if (j > 0) {
				entityIn.setSecondsOnFire(j * 4);
			}

			if (entityIn instanceof Player) {
				Player entityplayer = (Player) entityIn;
				ItemStack itemstack = this.getMainHandItem();
				ItemStack itemstack1 = entityplayer.isUsingItem() ? entityplayer.getUseItem() : ItemStack.EMPTY;

				if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem() instanceof AxeItem && itemstack1.getItem() instanceof ShieldItem) {
					float f1 = 0.25F + (float) EnchantmentHelper.getBlockEfficiency(this) * 0.05F;

					if (this.random.nextFloat() < f1) {
						entityplayer.getCooldowns().addCooldown(Items.SHIELD, 100);
						this.level().broadcastEntityEvent(entityplayer, (byte) 30);
					}
				}
			}

			this.doEnchantDamageEffects(this, entityIn);
		}

		return flag;
	}

	@Override
	public void tick() {
		super.tick();
		this.fallDistance = 0.0F;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return PathfinderMob.createMobAttributes()
				.add(Attributes.ATTACK_DAMAGE);
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		return new FlyingPathNavigation(this, level);
	}

	@Override
	public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	protected boolean isMovementNoisy() {
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

	public class FlyingMoveHelper extends MoveControl {
		private EntityMMFlyingCreature flyingEntity = EntityMMFlyingCreature.this;

		public FlyingMoveHelper() {
			super(EntityMMFlyingCreature.this);
		}

		@Override
		public void tick() {
			if (this.operation == MoveControl.Operation.MOVE_TO) {
				double d0 = this.wantedX + 0.5D - this.flyingEntity.getX();
				if (!this.flyingEntity.level().isEmptyBlock(BlockPos.containing(this.wantedX, this.wantedY - 1.0D, this.wantedZ))) {
					this.wantedY += 1.0D;
				}

				double d1 = this.wantedY + 1.0D - this.flyingEntity.getY();
				double d2 = this.wantedZ + 0.5D - this.flyingEntity.getZ();
				double d3 = d0 * d0 + d2 * d2;
				Vec3 currentMotion = this.flyingEntity.getDeltaMovement();
				float f = (float) (Mth.atan2(currentMotion.z, currentMotion.x) * (180D / Math.PI)) - 90.0F;
				float f1 = Mth.wrapDegrees(f - this.flyingEntity.getYRot());

				if (d3 > 1.0D) {
					double newMotionX = currentMotion.x + (Math.signum(d0) * 0.25D - currentMotion.x) * 0.1D;
					double newMotionZ = currentMotion.z + (Math.signum(d2) * 0.25D - currentMotion.z) * 0.1D;
					this.flyingEntity.setDeltaMovement(newMotionX, currentMotion.y, newMotionZ);
					this.flyingEntity.setYRot(this.flyingEntity.getYRot() + f1);
					this.flyingEntity.zza = 0.5F;
				}

				this.flyingEntity.setSpeed((float) (this.speedModifier * this.flyingEntity.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.6D));
				Vec3 updatedMotion = this.flyingEntity.getDeltaMovement();
				this.flyingEntity.setDeltaMovement(updatedMotion.x, updatedMotion.y + (double) this.flyingEntity.getSpeed() * d1 * 0.1D, updatedMotion.z);
			} else {
				this.flyingEntity.setSpeed(0.0F);
				this.flyingEntity.setDeltaMovement(0.0D, 0.0D, 0.0D);
			}
		}
	}
}
