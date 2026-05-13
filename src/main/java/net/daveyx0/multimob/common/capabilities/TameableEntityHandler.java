package net.daveyx0.multimob.common.capabilities;

import java.util.UUID;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import net.daveyx0.multimob.util.EntityUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class TameableEntityHandler implements ITameableEntity, INBTSerializable<CompoundTag> {
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

   @Override
   public CompoundTag serializeNBT(HolderLookup.Provider provider) {
      CompoundTag tag = new CompoundTag();
      if (this.ownerID != null) {
         tag.putUUID("Owner", this.ownerID);
      }
      tag.putBoolean("Tamed", this.isTamed);
      tag.putInt("FollowState", this.followState);
      return tag;
   }

   @Override
   public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
      if (nbt.hasUUID("Owner")) {
         this.ownerID = nbt.getUUID("Owner");
      } else {
         this.ownerID = null;
      }
      this.isTamed = nbt.getBoolean("Tamed");
      this.followState = nbt.getInt("FollowState");
   }

   private static class Factory implements Callable<ITameableEntity> {
      public ITameableEntity call() throws Exception {
         return new TameableEntityHandler();
      }
   }
}
