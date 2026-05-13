package net.daveyx0.multimob.client.renderer;

import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.util.ObfuscationReflectionHelper;

@OnlyIn(Dist.CLIENT)
public class MMRenderManager {
   public static void init() {
   }

   @SuppressWarnings("unchecked")
   public static void addRenderLayers(Class<? extends RenderLayer> layerClass) {
      Map<EntityType<?>, EntityRenderer<?>> rendererMap = ObfuscationReflectionHelper.getPrivateValue(EntityRenderDispatcher.class, Minecraft.getInstance().getEntityRenderDispatcher(), "renderers");
      if (rendererMap != null) {
         for (Map.Entry<EntityType<?>, EntityRenderer<?>> entry : rendererMap.entrySet()) {
            EntityRenderer<?> render = entry.getValue();
            if (render instanceof LivingEntityRenderer) {
               LivingEntityRenderer livingRenderer = (LivingEntityRenderer) render;
               RenderLayer layer = null;
               try {
                  layer = (RenderLayer) layerClass.getConstructor(LivingEntityRenderer.class).newInstance(render);
               } catch (Exception exception) {
                  exception.printStackTrace();
               }

               if (layer != null) {
                  livingRenderer.addLayer(layer);
               }
            }
         }
      }
   }

   @SuppressWarnings("unchecked")
   public static void addRenderLayersOldMethods(Class<? extends RenderLayer> layerClass) {
      Map<EntityType<?>, EntityRenderer<?>> rendererMap = ObfuscationReflectionHelper.getPrivateValue(EntityRenderDispatcher.class, Minecraft.getInstance().getEntityRenderDispatcher(), "renderers");
      if (rendererMap != null) {
         for (Map.Entry<EntityType<?>, EntityRenderer<?>> entry : rendererMap.entrySet()) {
            EntityRenderer<?> render = entry.getValue();
            if (render instanceof LivingEntityRenderer) {
               LivingEntityRenderer livingRenderer = (LivingEntityRenderer) render;
               RenderLayer layer = null;
               try {
                  layer = (RenderLayer) layerClass.getConstructor(LivingEntityRenderer.class).newInstance(render);
               } catch (Exception exception) {
                  exception.printStackTrace();
               }

               if (layer != null) {
                  livingRenderer.addLayer(layer);
               }
            }
         }
      }
   }
}
