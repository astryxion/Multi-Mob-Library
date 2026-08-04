package net.daveyx0.multimob.spawn;

import java.util.ArrayList;
import java.util.List;
import net.daveyx0.multimob.config.MMConfig;
import net.daveyx0.multimob.config.MMConfigSpawns;
import net.daveyx0.multimob.core.MultiMob;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class MMSpawnRegistry {
   public static final List<MMSpawnEntry> SPAWNS = new ArrayList<>();

   public static void registerFillerSpawns() {
      registerSpawnEntry((new MMConfigSpawnEntry("_Filler_MMMonster", "multimob:dummy", 100, false)).setupBaseMobSpawnEntry(false).setCreatureType("MULTIMOBMONSTER"));
      registerSpawnEntry((new MMConfigSpawnEntry("_Filler_MMPassive", "multimob:dummy", 50, false)).setupBaseMobSpawnEntry(false).setCreatureType("MULTIMOBPASSIVE"));
      registerSpawnEntry((new MMConfigSpawnEntry("_Filler_MMWater", "multimob:dummy", 50, false)).setupBaseMobSpawnEntry(false).setCreatureType("MULTIMOBWATER"));
      registerSpawnEntry((new MMConfigSpawnEntry("_Filler_MMLava", "multimob:dummy", 50, false)).setupBaseMobSpawnEntry(false).setCreatureType("MULTIMOBLAVA"));
   }

   public static List<MMSpawnEntry> getSpawnEntries() {
      return SPAWNS;
   }

   public static void loadSpawns() {
      SPAWNS.clear();

      for (MMConfigSpawnEntry configEntry : MMConfig.CONFIGSPAWNS) {
         MMSpawnEntry entry = getSpawnEntryFromConfig(configEntry);
         if (entry != null && entry.getEntityType() != null) {
            SPAWNS.add(entry);
         }
      }

      registerRegularSpawns();
   }

   public static void registerSpawnEntry(MMConfigSpawnEntry entry) {
      MMConfigSpawns.addConfigSpawnEntry(entry);
   }

   public static MMSpawnEntry getSpawnEntryFromEntityType(EntityType<?> entityType) {
      for (MMSpawnEntry entry : SPAWNS) {
         if (entry.getEntityType().equals(entityType)) {
            return entry;
         }
      }

      return null;
   }

   public static MMSpawnEntry getSpawnEntryFromConfig(MMConfigSpawnEntry configEntry) {
      EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.parse(configEntry.entityName)).orElse(null);
      if (entityType != null) {
         return new MMSpawnEntry(configEntry.getEntryName(), entityType, configEntry);
      } else {
         MultiMob.LOGGER.info("Was not able to make " + configEntry.getEntryName() + " entry for " + configEntry.entityName);
         return null;
      }
   }

   public static void registerRegularSpawns() {
      for (MMSpawnEntry entry : SPAWNS) {
         addRegularSpawn(entry);
      }
   }

   /**
    * Vanilla biome spawn lists are immutable; custom spawning is handled by {@link MMWorldSpawner}.
    */
   public static void addRegularSpawn(MMSpawnEntry entry) {
   }
}
