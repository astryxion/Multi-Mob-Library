package net.daveyx0.multimob.capabilities;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

/**
 * Legacy helper retained for API compatibility. NeoForge 1.21 uses EntityCapability + AttachmentType.
 */
public class CapabilityProviderSerializable<H> implements INBTSerializable<Tag> {
   protected final Direction facing;
   protected final H instance;

   public CapabilityProviderSerializable(Object capability, @Nullable Direction facing, @Nullable H instance) {
      this.facing = facing;
      this.instance = instance;
   }

   public CapabilityProviderSerializable(Object capability, @Nullable Direction facing) {
      this(capability, facing, null);
   }

   public CapabilityProviderSerializable(Object capability) {
      this(capability, null, null);
   }

   public Tag serializeNBT(HolderLookup.Provider provider) {
      if (this.instance instanceof INBTSerializable<?> serializable) {
         @SuppressWarnings("unchecked")
         INBTSerializable<Tag> typed = (INBTSerializable<Tag>) serializable;
         return typed.serializeNBT(provider);
      }
      return new CompoundTag();
   }

   public void deserializeNBT(HolderLookup.Provider provider, Tag nbt) {
      if (this.instance instanceof INBTSerializable<?> serializable) {
         @SuppressWarnings("unchecked")
         INBTSerializable<Tag> typed = (INBTSerializable<Tag>) serializable;
         typed.deserializeNBT(provider, nbt);
      }
   }

   @Nullable
   public Direction getFacing() {
      return this.facing;
   }

   @Nullable
   public final H getInstance() {
      return this.instance;
   }
}
