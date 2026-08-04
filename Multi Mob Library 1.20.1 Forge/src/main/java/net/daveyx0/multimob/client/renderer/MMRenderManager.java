package net.daveyx0.multimob.client.renderer;

import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MMRenderManager {

   public static void init() {
   }

   @SuppressWarnings("unchecked")
   public static void addRenderLayers(Class<? extends RenderLayer> layerClass) {
      for (Map.Entry<EntityType<?>, EntityRenderer<?>> entry : Minecraft.getInstance().getEntityRenderDispatcher().renderers.entrySet()) {
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

   @SuppressWarnings("unchecked")
   public static void addRenderLayersOldMethods(Class<? extends RenderLayer> layerClass) {
      for (Map.Entry<EntityType<?>, EntityRenderer<?>> entry : Minecraft.getInstance().getEntityRenderDispatcher().renderers.entrySet()) {
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
