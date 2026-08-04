package net.daveyx0.multimob.common.capabilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class VariantEntityHandler implements IVariantEntity, INBTSerializable<CompoundTag> {
   protected int variantId;

   public VariantEntityHandler() {
      this.variantId = 0;
   }

   public VariantEntityHandler(int id) {
      this.variantId = id;
   }

   public int getVariant() {
      return this.variantId;
   }

   public void setVariant(int id) {
      this.variantId = id;
   }

   @Override
   public CompoundTag serializeNBT(HolderLookup.Provider provider) {
      CompoundTag tag = new CompoundTag();
      tag.putInt("Variant", this.variantId);
      return tag;
   }

   @Override
   public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
      this.variantId = nbt.getInt("Variant");
   }
}
