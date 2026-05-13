package net.daveyx0.multimob.common.capabilities;

import java.util.UUID;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import net.daveyx0.multimob.util.EntityUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class TameableEntityHandler implements ITameableEntity {
   protected UUID ownerID;
   protected boolean isTamed;
   protected int followState;

   public TameableEntityHandler() {
      this.ownerID = null;
      this.isTamed = false;
      this.followState = 0;
   }

   public TameableEntityHandler(UUID id) {
      this.ownerID = id;
      this.isTamed = true;
      this.followState = 0;
   }

   @Nullable
   public LivingEntity getOwner(LivingEntity entityIn) {
      try {
         UUID uuid = this.getOwnerId();
         if (uuid != null) {
            Player player = entityIn.level().getPlayerByUUID(uuid);
            if (player != null) {
               return player;
            } else {
               LivingEntity entity = EntityUtil.getLoadedEntityByUUID(uuid, entityIn.level());
               return entity != null ? entity : null;
            }
         } else {
            return null;
         }
      } catch (IllegalArgumentException var5) {
         return null;
      }
   }

   public boolean isOwner(LivingEntity thisEntity, LivingEntity entityIn) {
      return entityIn == this.getOwner(thisEntity);
   }

   public void setOwner(UUID id) {
      this.ownerID = id;
   }

   public boolean isTamed() {
      return this.isTamed;
   }

   public void setTamed(boolean set) {
      this.isTamed = set;
   }

   public UUID getOwnerId() {
      return this.ownerID;
   }

   public int getFollowState() {
      return this.followState;
   }

   public void setFollowState(int set) {
      this.followState = set;
   }

   private static class Factory implements Callable<ITameableEntity> {
      public ITameableEntity call() throws Exception {
         return new TameableEntityHandler();
      }
   }
}
