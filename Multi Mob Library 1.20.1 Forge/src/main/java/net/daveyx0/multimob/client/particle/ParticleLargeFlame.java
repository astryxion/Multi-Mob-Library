package net.daveyx0.multimob.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;

public class ParticleLargeFlame extends TextureSheetParticle {
   public ParticleLargeFlame(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.quadSize = 3.0F;
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
   }

   @Override
   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.yd += 0.004D;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.96D;
         this.yd *= 0.96D;
         this.zd *= 0.96D;
         if (this.onGround) {
            this.xd *= 0.7D;
            this.zd *= 0.7D;
         }
      }
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }
}
