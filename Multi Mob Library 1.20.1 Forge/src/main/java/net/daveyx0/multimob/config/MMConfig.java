package net.daveyx0.multimob.config;

import java.util.HashSet;
import net.daveyx0.multimob.spawn.MMConfigSpawnEntry;
import net.daveyx0.multimob.spawn.MMSpawnRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class MMConfig {
   public static ForgeConfigSpec CONFIG_SPEC;
   public static final HashSet<MMConfigSpawnEntry> CONFIGSPAWNS = new HashSet();
   public static final HashSet<MMConfigSpawnEntry> EXTERNALCONFIGSPAWNS = new HashSet();

   static {
      ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
      MMConfigSpawns.buildConfig(builder);
      CONFIG_SPEC = builder.build();
   }

   public static void postInit() {
      reloadConfig();
   }

   private static void reloadConfig() {
      MMConfigSpawns.load();
      refreshConfigSpawns();

      MMSpawnRegistry.loadSpawns();
   }

   private static void refreshConfigSpawns() {
      HashSet<MMConfigSpawnEntry> oldConfigSpawns = new HashSet<>(CONFIGSPAWNS);
      CONFIGSPAWNS.clear();

      for(MMConfigSpawnEntry entry : EXTERNALCONFIGSPAWNS) {
         MMConfigSpawnEntry newEntry = entry;

         for(MMConfigSpawnEntry oldEntry : oldConfigSpawns) {
            if (oldEntry.getEntryName().equals(entry.getEntryName())) {
               newEntry = oldEntry;
            }
         }

         CONFIGSPAWNS.add(newEntry);
      }

      String[] configEntries = MMConfigSpawns.getConfigSpawnEntries();
      if (configEntries != null) {
         for(String configEntry : configEntries) {
            if (!configEntry.equals("")) {
               String[] names = configEntry.split("#");
               if (names == null || names.length >= 2) {
                  MMConfigSpawnEntry newEntry = new MMConfigSpawnEntry(names[0], names[1]);

                  for(MMConfigSpawnEntry oldEntry : oldConfigSpawns) {
                     if (oldEntry.getEntryName().equals(names[0])) {
                        newEntry = oldEntry;
                     }
                  }

                  CONFIGSPAWNS.add(newEntry);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onConfigChanged(ModConfigEvent event) {
      reloadConfig();
   }
}
