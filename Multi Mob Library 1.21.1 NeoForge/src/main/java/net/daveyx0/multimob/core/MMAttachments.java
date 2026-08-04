package net.daveyx0.multimob.core;

import java.util.function.Supplier;
import net.daveyx0.multimob.common.capabilities.TameableEntityHandler;
import net.daveyx0.multimob.common.capabilities.VariantEntityHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MMAttachments {
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
      DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "multimob");

   public static final DeferredHolder<AttachmentType<?>, AttachmentType<VariantEntityHandler>> VARIANT =
      ATTACHMENT_TYPES.register("variant", () -> AttachmentType.serializable((Supplier<VariantEntityHandler>) VariantEntityHandler::new).build());

   public static final DeferredHolder<AttachmentType<?>, AttachmentType<TameableEntityHandler>> TAMEABLE =
      ATTACHMENT_TYPES.register("tameable", () -> AttachmentType.serializable((Supplier<TameableEntityHandler>) TameableEntityHandler::new).build());

   public static void init(IEventBus modEventBus) {
      ATTACHMENT_TYPES.register(modEventBus);
   }
}
