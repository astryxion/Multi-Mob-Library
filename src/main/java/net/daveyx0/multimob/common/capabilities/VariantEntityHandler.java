package net.daveyx0.multimob.common.capabilities;

import java.util.concurrent.Callable;

public class VariantEntityHandler implements IVariantEntity {
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

   private static class Factory implements Callable<IVariantEntity> {
      public IVariantEntity call() throws Exception {
         return new VariantEntityHandler();
      }
   }
}
