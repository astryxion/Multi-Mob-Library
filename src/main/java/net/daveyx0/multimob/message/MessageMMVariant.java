package net.daveyx0.multimob.message;

import java.util.UUID;
import net.daveyx0.multimob.common.capabilities.CapabilityVariantEntity;
import net.daveyx0.multimob.common.capabilities.IVariantEntity;
import net.daveyx0.multimob.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageMMVariant implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<MessageMMVariant> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("multimob", "variant"));
   public static final StreamCodec<RegistryFriendlyByteBuf, MessageMMVariant> STREAM_CODEC = StreamCodec.ofMember(MessageMMVariant::encode, MessageMMVariant::decode);
   private String entityId;
   private int variantId;

   public MessageMMVariant() {
   }

   public MessageMMVariant(String entityInID, int variantId) {
      this.entityId = entityInID;
      this.variantId = variantId;
   }

   public static MessageMMVariant decode(FriendlyByteBuf buf) {
      MessageMMVariant msg = new MessageMMVariant();
      msg.entityId = buf.readUtf();
      msg.variantId = buf.readInt();
      return msg;
   }

   public static void encode(MessageMMVariant msg, FriendlyByteBuf buf) {
      buf.writeUtf(msg.entityId);
      buf.writeInt(msg.variantId);
   }

   public static void handle(MessageMMVariant message, IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         if (FMLEnvironment.dist.isClient()) {
            if (!message.entityId.isEmpty()) {
               LivingEntity entity = EntityUtil.getLoadedEntityByUUID(UUID.fromString(message.entityId), Minecraft.getInstance().level);
               if (entity != null) {
                  IVariantEntity variant = entity.getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY);
                  if (variant != null) {
                     variant.setVariant(message.variantId);
                  }
               }
            }
         }
      });
   }

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
