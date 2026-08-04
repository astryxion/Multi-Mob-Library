package net.daveyx0.multimob.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MMParticles {
   @OnlyIn(Dist.CLIENT)
   public static Particle spawnParticle(String par1Str, ClientLevel theWorld, double par2, double par4, double par6, double par8, double par10, double par12, float[] rgb) {
      Minecraft mc = Minecraft.getInstance();
      if (mc != null && mc.getCameraEntity() != null && mc.particleEngine != null) {
         int i = mc.options.particles().get().getId();
         if (i == 1 && theWorld.random.nextInt(3) == 0) {
            i = 2;
         }

         double d6 = mc.getCameraEntity().getX() - par2;
         double d7 = mc.getCameraEntity().getY() - par4;
         double d8 = mc.getCameraEntity().getZ() - par6;
         Particle particle = null;
         double d9 = (double)16.0F;
         if (d6 * d6 + d7 * d7 + d8 * d8 > d9 * d9) {
            return null;
         } else if (i > 1) {
            return null;
         } else {
            if (par1Str.equals("slime")) {
               particle = new ParticleBreakingColored(theWorld, par2, par4, par6, par8, par10, par12, Items.SNOWBALL, rgb[0], rgb[1], rgb[2]);
            } else if (par1Str.equals("flame")) {
               particle = new ParticleLargeFlame(theWorld, par2, par4, par6, par8, par10, par12);
            }

            if (particle != null) {
               mc.particleEngine.add(particle);
            }

            return particle;
         }
      } else {
         return null;
      }
   }
}
