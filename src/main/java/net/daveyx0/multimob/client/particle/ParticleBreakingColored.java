package net.daveyx0.multimob.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleBreakingColored extends TextureSheetParticle {
   public ParticleBreakingColored(ClientLevel worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, Item itemIn, float r, float g, float b) {
      super(worldIn, posXIn, posYIn, posZIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.setSprite(Minecraft.getInstance().getItemRenderer().getModel(new ItemStack(itemIn), worldIn, null, 0).getParticleIcon());
      this.rCol = r / 255.0F;
      this.gCol = g / 255.0F;
      this.bCol = b / 255.0F;
      this.gravity = 1.0F;
      this.quadSize /= 2.0F;
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.TERRAIN_SHEET;
   }
}
