package net.daveyx0.multimob.common.capabilities;

import net.minecraft.world.item.Item;

public class TameableEntityEntry {
   Item[] itemsUsedToTame = null;
   Item[] itemsUsedToHeal = null;
   float tamedNewHealth = 20.0F;
   int chanceToTame = 100;
   boolean canBeTamedWithItem = true;

   public TameableEntityEntry(Item[] tameItems, Item[] healItems, float newHealth, int tameChance, boolean conventionalTame) {
      this.itemsUsedToTame = tameItems;
      this.itemsUsedToHeal = healItems;
      this.tamedNewHealth = newHealth;
      this.chanceToTame = tameChance;
      this.canBeTamedWithItem = conventionalTame;
   }

   public Item[] getTameItems() {
      return this.itemsUsedToTame;
   }

   public Item[] getHealItems() {
      return this.itemsUsedToHeal;
   }

   public float getTamedHealth() {
      return this.tamedNewHealth;
   }

   public int getTameChance() {
      return this.chanceToTame;
   }

   public boolean getCanBeTamedWithItem() {
      return this.canBeTamedWithItem;
   }
}
