package net.daveyx0.multimob.client;

import java.util.List;
import java.util.Optional;
import com.mojang.blaze3d.systems.RenderSystem;
import net.daveyx0.multimob.client.renderer.entity.layer.LayerVariantOverlay;
import net.daveyx0.multimob.common.capabilities.CapabilityVariantEntity;
import net.daveyx0.multimob.common.capabilities.IVariantEntity;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

@EventBusSubscriber(modid = "multimob", value = Dist.CLIENT)
public class CapabilityVariantClientEvents {
   @SubscribeEvent
   public static void onRenderPre(RenderLivingEvent.Pre<?, ?> event) {
      if (CapabilityVariantEntity.EventHandler.hasVariant(event.getEntity())) {
         IVariantEntity variant = event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY);
         if (variant != null && variant.getVariant() != 0) {
            if (event.getRenderer() != null) {
               List<RenderLayer<?, ?>> layerRenderers = null;
               try {
                  layerRenderers = ObfuscationReflectionHelper.getPrivateValue(LivingEntityRenderer.class, event.getRenderer(), "layers");
               } catch (Exception e) {
                  e.printStackTrace();
               }

               if (layerRenderers != null) {
                  Optional<RenderLayer<?, ?>> layerEntity = layerRenderers.stream()
                     .filter((layerRenderer) -> layerRenderer instanceof LayerVariantOverlay)
                     .findFirst();
                  if (layerEntity.isEmpty()) {
                     event.getRenderer().addLayer(new LayerVariantOverlay(event.getRenderer()));
                  }
               }
            }

            RenderSystem.depthMask(false);
         }
      }
   }

   @SubscribeEvent
   public static void onRenderPost(RenderLivingEvent.Post<?, ?> event) {
      if (CapabilityVariantEntity.EventHandler.hasVariant(event.getEntity())) {
         IVariantEntity variant = event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY);
         if (variant != null && variant.getVariant() != 0) {
            RenderSystem.depthMask(true);
         }
      }
   }
}
