package net.daveyx0.multimob.entity;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public interface IMultiMobPassive {
   Predicate<Entity> MULTIMOB_SELECTOR = new Predicate<Entity>() {
      public boolean test(@Nullable Entity p_apply_1_) {
         return p_apply_1_ instanceof IMultiMobPassive;
      }
   };
}
