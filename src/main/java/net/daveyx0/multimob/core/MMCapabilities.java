package net.daveyx0.multimob.core;

import net.daveyx0.multimob.common.capabilities.CapabilityTameableEntity;
import net.daveyx0.multimob.common.capabilities.CapabilityVariantEntity;
import net.daveyx0.multimob.common.capabilities.ITameableEntity;
import net.daveyx0.multimob.common.capabilities.IVariantEntity;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class MMCapabilities {
   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.register(IVariantEntity.class);
      event.register(ITameableEntity.class);
   }
}
