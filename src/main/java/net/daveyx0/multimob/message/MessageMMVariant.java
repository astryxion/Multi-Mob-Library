package net.daveyx0.multimob.message;

import java.util.UUID;
import net.daveyx0.multimob.common.capabilities.CapabilityVariantEntity;
import net.daveyx0.multimob.common.capabilities.IVariantEntity;
import net.daveyx0.multimob.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MessageMMVariant(String entityId, int variantId) implements CustomPacketPayload {

   public static final CustomPacketPayload.Type<MessageMMVariant> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("multimob", "variant"));

   public static final StreamCodec<RegistryFriendlyByteBuf, MessageMMVariant> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, MessageMMVariant::entityId,
      ByteBufCodecs.INT, MessageMMVariant::variantId,
      MessageMMVariant::new
   );

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(MessageMMVariant message, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (!message.entityId.isEmpty()) {
            LivingEntity entity = EntityUtil.getLoadedEntityByUUID(UUID.fromString(message.entityId), Minecraft.getInstance().level);
            if (entity != null) {
               IVariantEntity variant = entity.getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY);
               if (variant != null) {
                  variant.setVariant(message.variantId);
               }
            }
         }
      });
   }
}
