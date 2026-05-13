package net.daveyx0.multimob.common.capabilities;

import java.util.List;
import java.util.Optional;
import net.daveyx0.multimob.client.renderer.entity.layer.LayerVariantOverlay;
import net.daveyx0.multimob.message.MMMessageRegistry;
import net.daveyx0.multimob.message.MessageMMVariant;
import net.daveyx0.multimob.network.MMNetworkWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.EntityCapability;

public class CapabilityVariantEntity {
   public static final EntityCapability<IVariantEntity, Void> VARIANT_ENTITY_CAPABILITY = EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath("multimob", "variant"), IVariantEntity.class);
   public static final ResourceLocation capabilityID = ResourceLocation.fromNamespaceAndPath("multimob", "variant");

   public static void register() {
   }

   @EventBusSubscriber(modid = "multimob")
   public static class EventHandler {
      @SubscribeEvent
      public static void LivingEntityEvent(EntityTickEvent.Post event) {
         if (hasVariant(event.getEntity()) && event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY) != null && event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY).getVariant() != 0 && event.getEntity().tickCount % 10 == 0) {
            IVariantEntity variant = event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY);
            MMMessageRegistry.getNetwork().sendToAll(new MessageMMVariant(event.getEntity().getUUID().toString(), variant.getVariant()));
         }
      }

      @SubscribeEvent
      @OnlyIn(Dist.CLIENT)
      public static void LivingLayerRenderEvent(RenderLivingEvent.Pre event) {
         if (hasVariant(event.getEntity()) && event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY) != null && event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY).getVariant() != 0) {
            if (event.getRenderer() != null) {
               List<RenderLayer> layerRenderers = null;
               try {
                  layerRenderers = ObfuscationReflectionHelper.getPrivateValue(LivingEntityRenderer.class, event.getRenderer(), "layers");
               } catch (Exception e) {
                  e.printStackTrace();
               }

               if (layerRenderers != null) {
                  Optional<RenderLayer> layerEntity = layerRenderers.stream().filter((layerRenderer) -> layerRenderer instanceof LayerVariantOverlay).findFirst();
                  if (layerEntity == null || !layerEntity.isPresent()) {
                     event.getRenderer().addLayer(new LayerVariantOverlay(event.getRenderer()));
                  }
               }
            }

            RenderSystem.depthMask(false);
         }
      }

      @SubscribeEvent
      @OnlyIn(Dist.CLIENT)
      public static void LivingLayerRenderEvent(RenderLivingEvent.Post event) {
         if (hasVariant(event.getEntity()) && event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY) != null && event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY).getVariant() != 0) {
            RenderSystem.depthMask(true);
         }
      }

      public static boolean hasVariant(Entity entity) {
         return entity != null && entity instanceof Mob && entity.getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY) != null;
      }
   }
}
