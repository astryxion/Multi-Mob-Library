package net.daveyx0.multimob.message;

import java.util.UUID;
import java.util.function.Supplier;
import net.daveyx0.multimob.common.capabilities.CapabilityTameableEntity;
import net.daveyx0.multimob.common.capabilities.ITameableEntity;
import net.daveyx0.multimob.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class MessageMMTameable {
   private String entityId;
   private String ownerId;
   private int followState;

   public MessageMMTameable() {
   }

   public MessageMMTameable(String entityInID, String summonerInID, int followState) {
      this.entityId = entityInID;
      this.ownerId = summonerInID;
      this.followState = followState;
   }

   public static MessageMMTameable decode(FriendlyByteBuf buf) {
      MessageMMTameable msg = new MessageMMTameable();
      msg.entityId = buf.readUtf();
      msg.ownerId = buf.readUtf();
      msg.followState = buf.readInt();
      return msg;
   }

   public static void encode(MessageMMTameable msg, FriendlyByteBuf buf) {
      buf.writeUtf(msg.entityId);
      buf.writeUtf(msg.ownerId);
      buf.writeInt(msg.followState);
   }

   public static void handle(MessageMMTameable message, Supplier<NetworkEvent.Context> ctx) {
      ctx.get().enqueueWork(() -> {
         DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (!message.entityId.isEmpty() && !message.ownerId.isEmpty()) {
               LivingEntity entity = EntityUtil.getLoadedEntityByUUID(UUID.fromString(message.entityId), Minecraft.getInstance().level);
               if (entity != null) {
                  entity.getCapability(CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY).ifPresent(tameable -> {
                     tameable.setTamed(true);
                     tameable.setOwner(UUID.fromString(message.ownerId));
                     tameable.setFollowState(message.followState);
                     CompoundTag nbttagcompound = entity.saveWithoutId(new CompoundTag());
                     nbttagcompound.putString("Owner", message.ownerId);
                     nbttagcompound.putString("OwnerUUID", message.ownerId);
                     nbttagcompound.putBoolean("Tame", true);
                     nbttagcompound.putBoolean("Tamed", true);
                     entity.load(nbttagcompound);
                  });
               }
            }
         });
      });
      ctx.get().setPacketHandled(true);
   }
}
