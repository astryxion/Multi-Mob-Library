package net.daveyx0.multimob.client.renderer;

import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * EntityRenderDispatcher.renderers is private in 1.21. Layer injection is handled per-renderer
 * via {@link net.daveyx0.multimob.client.CapabilityVariantClientEvents} instead.
 */
@OnlyIn(Dist.CLIENT)
public class MMRenderManager {

   public static void init() {
   }

   public static void addRenderLayers(Class<? extends RenderLayer> layerClass) {
   }

   public static void addRenderLayersOldMethods(Class<? extends RenderLayer> layerClass) {
   }
}
