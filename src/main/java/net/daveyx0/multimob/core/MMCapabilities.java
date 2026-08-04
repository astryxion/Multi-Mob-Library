package net.daveyx0.multimob.core;

import net.daveyx0.multimob.common.capabilities.CapabilityTameableEntity;
import net.daveyx0.multimob.common.capabilities.CapabilityVariantEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class MMCapabilities {
   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
         event.registerEntity(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY, type, (entity, ctx) -> {
            if (MMVariantEntries.variantEntries.containsKey(entity.getClass())) {
               return entity.getData(MMAttachments.VARIANT.get());
            }
            return null;
         });
         event.registerEntity(CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, type, (entity, ctx) -> {
            for (Class<?> tameableClass : MMTameableEntries.tameableEntries.keySet()) {
               if (tameableClass.isInstance(entity)) {
                  return entity.getData(MMAttachments.TAMEABLE.get());
               }
            }
            return null;
         });
      }
   }
}
