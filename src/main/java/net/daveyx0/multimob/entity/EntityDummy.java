package net.daveyx0.multimob.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class EntityDummy extends PathfinderMob {
   public EntityDummy(EntityType<? extends EntityDummy> entityType, Level worldIn) {
      super(entityType, worldIn);
   }
}
