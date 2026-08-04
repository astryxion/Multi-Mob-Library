package net.daveyx0.multimob.common.capabilities;

import java.util.List;
import java.util.Optional;
import net.daveyx0.multimob.capabilities.CapabilityProviderSerializable;
import net.daveyx0.multimob.client.renderer.entity.layer.LayerVariantOverlay;
import net.daveyx0.multimob.core.MMVariantEntries;
import net.daveyx0.multimob.message.MMMessageRegistry;
import net.daveyx0.multimob.message.MessageMMVariant;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

public class CapabilityVariantEntity {
   public static Capability<IVariantEntity> VARIANT_ENTITY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
   public static final ResourceLocation capabilityID = new ResourceLocation("multimob", "variant");

   public static void register() {
   }

   @Mod.EventBusSubscriber(modid = "multimob")
   public static class EventHandler {
      @SubscribeEvent
      public static void AttachEntityCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
         if (event.getObject() != null && MMVariantEntries.variantEntries.containsKey(((Entity)event.getObject()).getClass())) {
            event.addCapability(capabilityID, new CapabilityProviderSerializable<>(VARIANT_ENTITY_CAPABILITY, null, new VariantEntityHandler()));
         }
      }

      @SubscribeEvent
      public static void LivingEntityEvent(LivingEvent.LivingTickEvent event) {
         // Variant visuals are synced when a player starts tracking the entity.
      }

      @SubscribeEvent
      public static void PlayerStartsTrackingEvent(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
         if (!event.getEntity().level().isClientSide && hasVariant(event.getTarget())) {
            event.getTarget().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY).ifPresent(variant -> {
               if (variant.getVariant() != 0) {
                  MMMessageRegistry.getNetwork().send(PacketDistributor.TRACKING_ENTITY.with(() -> event.getTarget()), new MessageMMVariant(event.getTarget().getUUID().toString(), variant.getVariant()));
               }
            });
         }
      }

      @SubscribeEvent
      @OnlyIn(Dist.CLIENT)
      public static void LivingLayerRenderEvent(RenderLivingEvent.Pre event) {
         if (hasVariant(event.getEntity()) && event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY).orElse(null) != null && ((IVariantEntity)event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY).orElse(null)).getVariant() != 0) {
            if (event.getRenderer() != null) {
               List<RenderLayer> layerRenderers = null;
               try {
                  layerRenderers = ObfuscationReflectionHelper.getPrivateValue(LivingEntityRenderer.class, event.getRenderer(), "f_115291_");
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
         if (hasVariant(event.getEntity()) && event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY).orElse(null) != null && ((IVariantEntity)event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY).orElse(null)).getVariant() != 0) {
            RenderSystem.depthMask(true);
         }
      }

      public static boolean hasVariant(Entity entity) {
         return entity != null && entity instanceof Mob && entity.getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY).isPresent();
      }
   }
}
