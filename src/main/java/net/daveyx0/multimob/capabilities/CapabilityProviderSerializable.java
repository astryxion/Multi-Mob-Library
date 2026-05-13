package net.daveyx0.multimob.capabilities;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class CapabilityProviderSerializable<H> implements ICapabilityProvider<Entity, Void, H>, INBTSerializable<Tag> {
   protected final EntityCapability<H, Void> capability;
   protected final Direction facing;
   protected final H instance;

   public CapabilityProviderSerializable(EntityCapability<H, Void> capability, @Nullable Direction facing, @Nullable H instance) {
      this.capability = capability;
      this.facing = facing;
      this.instance = instance;
   }

   public CapabilityProviderSerializable(EntityCapability<H, Void> capability, @Nullable Direction facing) {
      this(capability, facing, null);
   }

   public CapabilityProviderSerializable(EntityCapability<H, Void> capability) {
      this(capability, (Direction)null, null);
   }

   @Nullable
   public H getCapability(Entity object, Void context) {
      return this.instance;
   }

   public Tag serializeNBT(HolderLookup.Provider provider) {
      if (this.instance instanceof INBTSerializable) {
         return ((INBTSerializable)this.instance).serializeNBT(provider);
      }
      return new CompoundTag();
   }

   public void deserializeNBT(HolderLookup.Provider provider, Tag nbt) {
      if (this.instance instanceof INBTSerializable) {
         ((INBTSerializable)this.instance).deserializeNBT(provider, nbt);
      }
   }

   @Nullable
   public Direction getFacing() {
      return this.facing;
   }

   public final EntityCapability<H, Void> getCapabilityInstance() {
      return this.capability;
   }

   @Nullable
   public final H getInstance() {
      return this.instance;
   }
}
