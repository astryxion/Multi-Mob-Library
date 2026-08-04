package net.daveyx0.multimob.message;

import java.util.UUID;
import net.daveyx0.multimob.common.capabilities.CapabilityTameableEntity;
import net.daveyx0.multimob.common.capabilities.ITameableEntity;
import net.daveyx0.multimob.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MessageMMTameable(String entityId, String ownerId, int followState) implements CustomPacketPayload {

   public static final CustomPacketPayload.Type<MessageMMTameable> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("multimob", "tameable"));

   public static final StreamCodec<RegistryFriendlyByteBuf, MessageMMTameable> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, MessageMMTameable::entityId,
      ByteBufCodecs.STRING_UTF8, MessageMMTameable::ownerId,
      ByteBufCodecs.INT, MessageMMTameable::followState,
      MessageMMTameable::new
   );

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(MessageMMTameable message, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (!message.entityId.isEmpty() && !message.ownerId.isEmpty()) {
            LivingEntity entity = EntityUtil.getLoadedEntityByUUID(UUID.fromString(message.entityId), Minecraft.getInstance().level);
            if (entity != null) {
               ITameableEntity tameable = entity.getCapability(CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY);
               if (tameable != null) {
                  tameable.setTamed(true);
                  tameable.setOwner(UUID.fromString(message.ownerId));
                  tameable.setFollowState(message.followState);
                  CompoundTag nbttagcompound = entity.saveWithoutId(new CompoundTag());
                  nbttagcompound.putString("Owner", message.ownerId);
                  nbttagcompound.putString("OwnerUUID", message.ownerId);
                  nbttagcompound.putBoolean("Tame", true);
                  nbttagcompound.putBoolean("Tamed", true);
                  entity.load(nbttagcompound);
               }
            }
         }
      });
   }
}
