package net.daveyx0.multimob.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.daveyx0.multimob.variant.MMVariantEntityEntry;
import net.minecraft.world.entity.Entity;

public class MMVariantEntries {
   public static final Map<Class<? extends Entity>, List<MMVariantEntityEntry>> variantEntries = new HashMap();

   public static void registerVariants() {
   }

   public static void addVariant(Class<? extends Entity> entityClass, int variantID, String variantName) {
      MMVariantEntityEntry entry = new MMVariantEntityEntry(variantID, variantName);
      List<MMVariantEntityEntry> variantList = new ArrayList();
      if (variantEntries.containsKey(entityClass)) {
         variantList = (List)variantEntries.get(entityClass);
      }

      variantList.add(entry);
      variantEntries.put(entityClass, variantList);
   }

   public static MMVariantEntityEntry getVariantEntry(Class<? extends Entity> entityClass, int id) {
      List<MMVariantEntityEntry> variants = (List)variantEntries.get(entityClass);
      if (variants != null && !variants.isEmpty()) {
         for(MMVariantEntityEntry variant : variants) {
            if (variant.getVariantIndex() == id) {
               return variant;
            }
         }
      }

      return null;
   }
}
