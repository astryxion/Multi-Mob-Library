package net.daveyx0.multimob.spawn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.daveyx0.multimob.config.MMConfig;
import net.daveyx0.multimob.config.MMConfigSpawns;
import net.daveyx0.multimob.core.MultiMob;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.daveyx0.multimob.util.FileUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.BuiltInRegistries;

public class MMSpawnRegistry {
   public static final List<MMSpawnEntry> SPAWNS = new ArrayList();

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

      for(MMConfigSpawnEntry configEntry : MMConfig.CONFIGSPAWNS) {
         MMSpawnEntry entry = getSpawnEntryFromConfig(configEntry);
         if (entry != null && entry.getEntityType() != null) {
            SPAWNS.add(getSpawnEntryFromConfig(configEntry));
         }
      }

      registerRegularSpawns();
   }

   public static void registerSpawnEntry(MMConfigSpawnEntry entry) {
      MMConfigSpawns.addConfigSpawnEntry(entry);
   }

   public static MMSpawnEntry getSpawnEntryFromEntityType(EntityType<?> entityType) {
      for(MMSpawnEntry entry : SPAWNS) {
         if (entry.getEntityType().equals(entityType)) {
            return entry;
         }
      }

      return null;
   }

   public static MMSpawnEntry getSpawnEntryFromConfig(MMConfigSpawnEntry configEntry) {
      EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(configEntry.entityName));
      if (entityType != null) {
         return new MMSpawnEntry(configEntry.getEntryName(), entityType, configEntry);
      } else {
         MultiMob.LOGGER.info("Was not able to make " + configEntry.getEntryName() + " entry for " + configEntry.entityName);
         return null;
      }
   }

   public static void registerRegularSpawns() {
      for(MMSpawnEntry entry : SPAWNS) {
         addRegularSpawn(entry);
      }

   }

   public static void addRegularSpawn(MMSpawnEntry entry) {
      if (entry.getSpawnLimit() != 0 && entry.getSpawnWeight() != 0) {
         HolderLookup.RegistryLookup<Biome> biomeRegistry = FileUtil.getBiomeLookup();
         if ((entry.getBiomes() == null || entry.getBiomes().isEmpty()) && (entry.getBiomeTypes() == null || entry.getBiomeTypes().isEmpty())) {
            for(Biome biome : biomeRegistry.listElements().map(Holder::value).toList()) {
               // Biome spawn data is now immutable in 1.20.1; spawn additions are handled via BiomeModifier or events
            }
         } else {
            Set<Biome> biomes = new HashSet();
            if (entry.getBiomes() != null && !entry.getBiomes().isEmpty()) {
               for(Biome biome : entry.getBiomes()) {
                  biomes.add(biome);
               }
            }

            if (entry.getBiomeTypes() != null && !entry.getBiomeTypes().isEmpty()) {
               for(Holder.Reference<Biome> biomeHolder : biomeRegistry.listElements().toList()) {
                  Biome biome = biomeHolder.value();
                  boolean typeCheck = true;
                  for(TagKey<Biome> biomeType : entry.getBiomeTypes()) {
                     if (!biomeHolder.is(biomeType)) {
                        typeCheck = false;
                     }
                  }

                  if (typeCheck) {
                     biomes.add(biome);
                  }
               }
            }
         }
      }

   }
}
