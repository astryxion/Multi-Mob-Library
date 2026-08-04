package net.daveyx0.multimob.message;

import java.util.UUID;
import java.util.function.Supplier;
import net.daveyx0.multimob.common.capabilities.CapabilityVariantEntity;
import net.daveyx0.multimob.common.capabilities.IVariantEntity;
import net.daveyx0.multimob.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class MessageMMVariant {
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

   public static void handle(MessageMMVariant message, Supplier<NetworkEvent.Context> ctx) {
      ctx.get().enqueueWork(() -> {
         DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (!message.entityId.isEmpty()) {
               LivingEntity entity = EntityUtil.getLoadedEntityByUUID(UUID.fromString(message.entityId), Minecraft.getInstance().level);
               if (entity != null) {
                  entity.getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY).ifPresent(variant -> {
                     variant.setVariant(message.variantId);
                  });
               }
            }
         });
      });
      ctx.get().setPacketHandled(true);
   }
}
