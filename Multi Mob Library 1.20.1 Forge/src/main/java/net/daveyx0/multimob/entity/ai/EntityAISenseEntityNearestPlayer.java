package net.daveyx0.multimob.entity.ai;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAISenseEntityNearestPlayer extends Goal {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Mob entityLiving;
   private final Predicate<Entity> predicate;
   private final Comparator<Entity> sorter;
   private LivingEntity entityTarget;
   private int maxRange;

   public EntityAISenseEntityNearestPlayer(Mob entityLivingIn, int range) {
      this.entityLiving = entityLivingIn;
      this.maxRange = range;
      this.predicate = new Predicate<Entity>() {
         @Override
         public boolean test(@Nullable Entity p_apply_1_) {
            if (!(p_apply_1_ instanceof Player)) {
               return false;
            } else if (((Player)p_apply_1_).getAbilities().invulnerable) {
               return false;
            } else {
               double d0 = EntityAISenseEntityNearestPlayer.this.maxTargetRange();
               if (p_apply_1_.isShiftKeyDown()) {
                  d0 *= 0.8D;
               }

               if (p_apply_1_.isInvisible()) {
                  float f = ((Player)p_apply_1_).getArmorCoverPercentage();
                  if (f < 0.1F) {
                     f = 0.1F;
                  }

                  d0 *= (double)(0.7F * f);
               }

               return (double)p_apply_1_.distanceTo(EntityAISenseEntityNearestPlayer.this.entityLiving) > d0 ? false : EntityAICustomTarget.isSuitableTarget(EntityAISenseEntityNearestPlayer.this.entityLiving, (LivingEntity)p_apply_1_, false, false);
            }
         }
      };
      this.sorter = Comparator.comparingDouble(entityLivingIn::distanceToSqr);
   }

   @Override
   public boolean canUse() {
      double d0 = this.maxTargetRange();
      List<Player> list = this.entityLiving.level().getEntitiesOfClass(Player.class, this.entityLiving.getBoundingBox().inflate(d0, 4.0D, d0), this.predicate::test);
      list.sort(this.sorter);
      if (list.isEmpty()) {
         return false;
      } else {
         this.entityTarget = list.get(0);
         return true;
      }
   }

   @Override
   public boolean canContinueToUse() {
      LivingEntity entitylivingbase = this.entityLiving.getTarget();
      if (entitylivingbase == null) {
         return false;
      } else if (!entitylivingbase.isAlive()) {
         return false;
      } else if (entitylivingbase instanceof Player && ((Player)entitylivingbase).getAbilities().invulnerable) {
         return false;
      } else {
         Team team = this.entityLiving.getTeam();
         Team team1 = entitylivingbase.getTeam();
         if (team != null && team1 == team) {
            return false;
         } else {
            double d0 = this.maxTargetRange();
            if (this.entityLiving.distanceToSqr(entitylivingbase) > d0 * d0) {
               return false;
            } else {
               return !(entitylivingbase instanceof ServerPlayer) || !((ServerPlayer)entitylivingbase).gameMode.isCreative();
            }
         }
      }
   }

   @Override
   public void start() {
      this.entityLiving.setTarget(this.entityTarget);
      super.start();
   }

   @Override
   public void stop() {
      this.entityLiving.setTarget((LivingEntity)null);
      super.start();
   }

   protected double maxTargetRange() {
      return (double)this.maxRange;
   }
}
