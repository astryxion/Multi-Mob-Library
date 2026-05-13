package net.daveyx0.multimob.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.daveyx0.multimob.common.capabilities.CapabilityVariantEntity;
import net.daveyx0.multimob.common.capabilities.IVariantEntity;
import net.daveyx0.multimob.core.MMVariantEntries;
import net.daveyx0.multimob.util.EntityUtil;
import net.daveyx0.multimob.variant.MMVariantEntityEntry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class LayerVariantOverlay<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
   private final LivingEntityRenderer<T, M> livingRenderer;

   @SuppressWarnings("unchecked")
   public LayerVariantOverlay(LivingEntityRenderer<?, ?> renderLivingBase) {
      super((LivingEntityRenderer<T, M>) renderLivingBase);
      this.livingRenderer = (LivingEntityRenderer<T, M>) renderLivingBase;
   }

   @Override
   public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      if (CapabilityVariantEntity.EventHandler.hasVariant(entitylivingbaseIn)) {
         IVariantEntity variant = (IVariantEntity)EntityUtil.getCapability(entitylivingbaseIn, CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY, null);
         if (variant != null && variant.getVariant() != 0) {
            MMVariantEntityEntry entry = MMVariantEntries.getVariantEntry(entitylivingbaseIn.getClass(), variant.getVariant());
            if (entry != null) {
               poseStack.pushPose();
               RenderSystem.depthMask(true);
               ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("multimob", "textures/entity/variants/" + entry.getVariantName() + ".png");
               VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
               this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
               RenderSystem.depthMask(false);
               poseStack.popPose();
            }
         }
      }
   }
}
