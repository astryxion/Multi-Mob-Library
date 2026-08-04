package net.daveyx0.multimob.common.capabilities;

import java.util.UUID;
import javax.annotation.Nullable;
import net.daveyx0.multimob.util.EntityUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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
      CompoundTag compound = new CompoundTag();
      UUID owner = this.getOwnerId();
      if (owner == null) {
         compound.putString("OwnerUUID", "");
      } else {
         compound.putString("OwnerUUID", owner.toString());
      }

      compound.putBoolean("Tamed", this.isTamed);
      compound.putInt("FollowState", this.followState);
      return compound;
   }

   @Override
   public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compound) {
      String ownerId = compound.getString("OwnerUUID");
      if (!ownerId.isEmpty()) {
         try {
            this.setOwner(UUID.fromString(ownerId));
            this.setTamed(true);
         } catch (IllegalArgumentException ignored) {
            this.setTamed(false);
            this.ownerID = null;
         }
      } else {
         this.ownerID = null;
         this.isTamed = compound.getBoolean("Tamed");
      }

      this.followState = compound.getInt("FollowState");
   }
}
