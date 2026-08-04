package net.daveyx0.multimob.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * Minimal 1.21 shell. Full swimming combat helpers from 1.20.1 were stubbed due to enchantment API remaps.
 */
public class EntityMMSwimmingCreature extends PathfinderMob {

   public EntityMMSwimmingCreature(EntityType<? extends EntityMMSwimmingCreature> type, Level level) {
      super(type, level);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return PathfinderMob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 10.0D)
         .add(Attributes.MOVEMENT_SPEED, 0.2D);
   }

   public boolean isInFlowingWater() {
      return false;
   }
}
