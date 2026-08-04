package net.daveyx0.multimob.common.capabilities;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;

public interface ITameableEntity {
   boolean isTamed();

   void setTamed(boolean var1);

   @Nullable
   LivingEntity getOwner(LivingEntity var1);

   UUID getOwnerId();

   void setOwner(UUID var1);

   int getFollowState();

   void setFollowState(int var1);
}
