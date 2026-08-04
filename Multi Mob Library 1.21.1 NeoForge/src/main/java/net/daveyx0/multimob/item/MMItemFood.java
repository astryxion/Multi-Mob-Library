package net.daveyx0.multimob.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class MMItemFood extends Item {
   public MMItemFood(int amount, float saturation, boolean isWolfFood, Item.Properties properties) {
      super(properties.food(new FoodProperties.Builder()
         .nutrition(amount)
         .saturationModifier(saturation)
         .build()));
   }
}
