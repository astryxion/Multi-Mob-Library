package net.daveyx0.multimob.client;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.daveyx0.multimob.core.MMItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@OnlyIn(Dist.CLIENT)
public class MMItemModelManager {
   public static final MMItemModelManager INSTANCE = new MMItemModelManager();
   public static final Set<Item> ITEMS = new HashSet<>();
   private final Set<Item> itemsRegistered = new HashSet<>();

   private MMItemModelManager() {
   }

   @SubscribeEvent
   public void registerModels(ModelEvent.RegisterAdditional event) {
      for (Item item : MMItemRegistry.ITEMS) {
         ITEMS.add(item);
      }

      this.registerItemModels();
   }

   private void registerItemModels() {
      ITEMS.stream().filter((item) -> !this.itemsRegistered.contains(item)).forEach(this::registerItemModel);
   }

   public void registerItemColors(Item[] items) {
      this.registerItemColor((stack, tintIndex) -> tintIndex > 0 ? 0xFFFFFFFF : MMItemModelManager.getColor(stack), items);
   }

   private void registerItemColor(ItemColor itemcolor, Item... itemsIn) {
      Minecraft.getInstance().getItemColors().register(itemcolor, itemsIn);
   }

   private void registerItemModel(Item item) {
      ResourceLocation registryName = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
      this.registerItemModel(item, registryName.toString());
   }

   private void registerItemModel(Item item, String modelLocation) {
      ModelResourceLocation fullModelLocation = ModelResourceLocation.inventory(ResourceLocation.parse(modelLocation));
      this.registerItemModel(item, fullModelLocation);
   }

   private void registerItemModel(Item item, ModelResourceLocation fullModelLocation) {
      this.itemsRegistered.add(item);
   }

   public static int getColor(ItemStack stack) {
      // 1.21 item colors are ARGB; RGB-only values (alpha 0) render fully invisible.
      DyedItemColor dyed = stack.get(DataComponents.DYED_COLOR);
      return dyed != null ? (0xFF000000 | dyed.rgb()) : 0xFFFFFFFF;
   }
}
