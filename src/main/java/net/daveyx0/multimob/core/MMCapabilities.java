package net.daveyx0.multimob.core;

import net.daveyx0.multimob.common.capabilities.CapabilityTameableEntity;
import net.daveyx0.multimob.common.capabilities.CapabilityVariantEntity;
import net.minecraft.nbt.CompoundTag;
import net.daveyx0.multimob.common.capabilities.TameableEntityHandler;
import net.daveyx0.multimob.common.capabilities.VariantEntityHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MMCapabilities {
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "multimob");
   public static final DeferredHolder<AttachmentType<?>, AttachmentType<VariantEntityHandler>> VARIANT_ATTACHMENT = ATTACHMENT_TYPES.register("variant", () -> AttachmentType.<CompoundTag, VariantEntityHandler>serializable((java.util.function.Supplier<VariantEntityHandler>)VariantEntityHandler::new).build());
   public static final DeferredHolder<AttachmentType<?>, AttachmentType<TameableEntityHandler>> TAMEABLE_ATTACHMENT = ATTACHMENT_TYPES.register("tameable", () -> AttachmentType.<CompoundTag, TameableEntityHandler>serializable((java.util.function.Supplier<TameableEntityHandler>)TameableEntityHandler::new).build());

   public static void registerAttachments(IEventBus modEventBus) {
      ATTACHMENT_TYPES.register(modEventBus);
   }

   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
         event.registerEntity(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY, entityType, (entity, context) -> {
            if (MMVariantEntries.variantEntries.containsKey(entity.getClass())) {
               return entity.getData(VARIANT_ATTACHMENT);
            } else {
               return null;
            }
         });
         event.registerEntity(CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, entityType, (entity, context) -> {
            if (MMTameableEntries.tameableEntries.containsKey(entity.getClass())) {
               return entity.getData(TAMEABLE_ATTACHMENT);
            } else {
               return null;
            }
         });
      }
   }
}
