package net.daveyx0.multimob.capabilities;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityProviderSerializable<H> implements ICapabilityProvider, INBTSerializable<Tag> {
   protected final Capability<H> capability;
   protected final Direction facing;
   protected final H instance;
   protected final LazyOptional<H> lazyOptional;

   public CapabilityProviderSerializable(Capability<H> capability, @Nullable Direction facing, @Nullable H instance) {
      this.capability = capability;
      this.facing = facing;
      this.instance = instance;
      this.lazyOptional = LazyOptional.of(() -> this.instance);
   }

   public CapabilityProviderSerializable(Capability<H> capability, @Nullable Direction facing) {
      this(capability, facing, null);
   }

   public CapabilityProviderSerializable(Capability<H> capability) {
      this(capability, (Direction)null, null);
   }

   public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
      if (capability == this.getCapabilityInstance()) {
         return this.lazyOptional.cast();
      }
      return LazyOptional.empty();
   }

   public Tag serializeNBT() {
      if (this.instance instanceof INBTSerializable) {
         return ((INBTSerializable)this.instance).serializeNBT();
      }
      return new CompoundTag();
   }

   public void deserializeNBT(Tag nbt) {
      if (this.instance instanceof INBTSerializable) {
         ((INBTSerializable)this.instance).deserializeNBT(nbt);
      }
   }

   @Nullable
   public Direction getFacing() {
      return this.facing;
   }

   public final Capability<H> getCapabilityInstance() {
      return this.capability;
   }

   @Nullable
   public final H getInstance() {
      return this.instance;
   }
}
