package net.daveyx0.multimob.core;

import java.util.HashMap;
import net.daveyx0.multimob.entity.EntityDummy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MMEntityRegistry {
   public static final HashMap<Class<? extends Entity>, Boolean> entities = new HashMap();

   public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "multimob");

   public static final RegistryObject<EntityType<EntityDummy>> DUMMY = ENTITY_TYPES.register("dummy",
      () -> EntityType.Builder.<EntityDummy>of(EntityDummy::new, MobCategory.MISC)
         .sized(0.6F, 1.8F)
         .clientTrackingRange(80)
         .updateInterval(3)
         .build("multimob:dummy"));

   public static void init(IEventBus modEventBus) {
      ENTITY_TYPES.register(modEventBus);
   }

   public static void addDummy() {
      entities.put(EntityDummy.class, true);
   }

   public static void addEntities(String modID, Class<? extends Entity> var1, String name1, int entityid, int bkEggColor, int fgEggColor, boolean flag) {
      entities.put(var1, flag);
   }

   public static void addEntitiesWithoutEgg(String modID, Class<? extends Entity> var1, String name1, int entityid, boolean flag) {
      entities.put(var1, flag);
   }

   public static void addCustomEntities(String modID, Class<? extends Entity> var1, String name1, int entityid, int track, int freq, boolean vel) {
   }
}
