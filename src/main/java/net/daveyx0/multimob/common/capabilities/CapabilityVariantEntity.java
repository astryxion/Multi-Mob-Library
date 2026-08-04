package net.daveyx0.multimob.common.capabilities;

import net.daveyx0.multimob.core.MMAttachments;
import net.daveyx0.multimob.message.MMMessageRegistry;
import net.daveyx0.multimob.message.MessageMMVariant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CapabilityVariantEntity {
   public static final DeferredHolder<AttachmentType<?>, AttachmentType<VariantEntityHandler>> VARIANT_ATTACHMENT = MMAttachments.VARIANT;

   public static final EntityCapability<IVariantEntity, Void> VARIANT_ENTITY_CAPABILITY =
      EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath("multimob", "variant"), IVariantEntity.class);

   public static final ResourceLocation capabilityID = ResourceLocation.fromNamespaceAndPath("multimob", "variant");

   public static void register() {
   }

   @EventBusSubscriber(modid = "multimob")
   public static class EventHandler {
      @SubscribeEvent
      public static void PlayerStartsTrackingEvent(PlayerEvent.StartTracking event) {
         if (!event.getEntity().level().isClientSide && hasVariant(event.getTarget())) {
            IVariantEntity variant = event.getTarget().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY);
            if (variant != null && variant.getVariant() != 0) {
               MMMessageRegistry.getNetwork().sendToTracking(event.getTarget(),
                  new MessageMMVariant(event.getTarget().getUUID().toString(), variant.getVariant()));
            }
         }
      }

      public static boolean hasVariant(Entity entity) {
         return entity instanceof Mob && entity.getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY) != null;
      }
   }
}
