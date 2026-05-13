package net.daveyx0.multimob.config;

import java.util.ArrayList;
import java.util.List;
import net.daveyx0.multimob.core.MultiMob;
import net.daveyx0.multimob.spawn.MMConfigSpawnEntry;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.common.ForgeConfigSpec;

public class MMConfigSpawns {
   private static List<String> defaultEntitiesToSpawn = new ArrayList();
   private static String[] entitiesToSpawn;
   private static int spawnTickDelay;
   private static boolean spawnInSpectate;
   private static boolean useAdditionalSpawning;
   private static int monsterSpawnLimit;
   private static int passiveSpawnLimit;
   private static int waterSpawnLimit;
   private static int lavaSpawnLimit;
   private static int vanillaMonsterSpawnLimit;
   private static int vanillaCreatureSpawnLimit;
   private static int vanillaAmbientSpawnLimit;
   private static int vanillaWaterSpawnLimit;
   private static int otherSpawnLimit;
   private static int[] dimensionWhiteList;
   private static int[] defaultWhiteList = new int[]{0};

   public static ForgeConfigSpec.IntValue SPAWN_TICK_DELAY;
   public static ForgeConfigSpec.BooleanValue SPAWN_IN_SPECTATE;
   public static ForgeConfigSpec.BooleanValue USE_ADDITIONAL_SPAWNING;
   public static ForgeConfigSpec.IntValue MONSTER_SPAWN_LIMIT;
   public static ForgeConfigSpec.IntValue PASSIVE_SPAWN_LIMIT;
   public static ForgeConfigSpec.IntValue WATER_SPAWN_LIMIT;
   public static ForgeConfigSpec.IntValue LAVA_SPAWN_LIMIT;
   public static ForgeConfigSpec.IntValue VANILLA_MONSTER_SPAWN_LIMIT;
   public static ForgeConfigSpec.IntValue VANILLA_CREATURE_SPAWN_LIMIT;
   public static ForgeConfigSpec.IntValue VANILLA_AMBIENT_SPAWN_LIMIT;
   public static ForgeConfigSpec.IntValue VANILLA_WATER_SPAWN_LIMIT;
   public static ForgeConfigSpec.IntValue OTHER_SPAWN_LIMIT;
   public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> DIMENSION_WHITELIST;
   public static ForgeConfigSpec.ConfigValue<List<? extends String>> ENTITIES_TO_SPAWN;

   public static void buildConfig(ForgeConfigSpec.Builder builder) {
      builder.comment("These options only apply when using the Additional Spawning system, which can also be enabled here.")
         .push("additionalSpawningOptions");

      SPAWN_TICK_DELAY = builder
         .comment("The Tick delay between spawn attempts for MultiMob. Lower for more common spawns.")
         .defineInRange("spawnTickDelay", 10, 1, Integer.MAX_VALUE);
      SPAWN_IN_SPECTATE = builder
         .comment("Enable/Disable allowing the MultiMob system to spawn while in spectator mode.")
         .define("spawnInSpectate", false);
      USE_ADDITIONAL_SPAWNING = builder
         .comment("Enable/Disable to activate an additional spawner to add more mobs to the world.")
         .define("useAdditionalSpawning", false);
      MONSTER_SPAWN_LIMIT = builder
         .comment("Determines how many additional mobs can spawn of the creature type: MultiMob Monster.")
         .defineInRange("monsterSpawnLimit", 0, 0, Integer.MAX_VALUE);
      PASSIVE_SPAWN_LIMIT = builder
         .comment("Determines how many additional mobs can spawn of the creature type: MultiMob Passive.")
         .defineInRange("passiveSpawnLimit", 0, 0, Integer.MAX_VALUE);
      WATER_SPAWN_LIMIT = builder
         .comment("Determines how many additional mobs can spawn of the creature type: MultiMob Water.")
         .defineInRange("waterSpawnLimit", 0, 0, Integer.MAX_VALUE);
      LAVA_SPAWN_LIMIT = builder
         .comment("Determines how many additional mobs can spawn of the creature type: MultiMob Lava.")
         .defineInRange("lavaSpawnLimit", 0, 0, Integer.MAX_VALUE);
      VANILLA_MONSTER_SPAWN_LIMIT = builder
         .comment("Determines how many additional mobs can spawn of the creature type: Monster.")
         .defineInRange("vanillaMonsterSpawnLimit", 0, 0, Integer.MAX_VALUE);
      VANILLA_AMBIENT_SPAWN_LIMIT = builder
         .comment("Determines how many additional mobs can spawn of the creature type: Ambient.")
         .defineInRange("vanillaAmbientSpawnLimit", 0, 0, Integer.MAX_VALUE);
      VANILLA_CREATURE_SPAWN_LIMIT = builder
         .comment("Determines how many additional mobs can spawn of the creature type: Creature.")
         .defineInRange("vanillaCreatureSpawnLimit", 0, 0, Integer.MAX_VALUE);
      VANILLA_WATER_SPAWN_LIMIT = builder
         .comment("Determines how many additional mobs can spawn of the creature type: Water.")
         .defineInRange("vanillaWaterSpawnLimit", 0, 0, Integer.MAX_VALUE);
      OTHER_SPAWN_LIMIT = builder
         .comment("Determines how many additional mobs can spawn of creature types from other mods.")
         .defineInRange("otherSpawnLimit", 0, 0, Integer.MAX_VALUE);

      List<Integer> defaultDimList = new ArrayList<>();
      defaultDimList.add(0);
      DIMENSION_WHITELIST = builder
         .comment("Determines which dimensions the additional spawns will count for.")
         .defineList("dimensionWhiteList", defaultDimList, o -> o instanceof Integer);

      builder.pop();

      builder.comment("Add additional spawn entries. Simply add a name for the entry, then '#' followed by the mob resource name (like minecraft:zombie) and it generates a new entry (go back to the main menu to refresh)")
         .push("extraEntries");

      List<String> defaultEntities = new ArrayList<>();
      defaultEntities.add("");
      ENTITIES_TO_SPAWN = builder
         .comment("Mob spawns")
         .defineList("entitiesToSpawn", defaultEntities, o -> o instanceof String);

      builder.pop();
   }

   public static void loadGeneralOptions() {
      spawnTickDelay = SPAWN_TICK_DELAY.get();
      spawnInSpectate = SPAWN_IN_SPECTATE.get();
      useAdditionalSpawning = USE_ADDITIONAL_SPAWNING.get();
      monsterSpawnLimit = MONSTER_SPAWN_LIMIT.get();
      passiveSpawnLimit = PASSIVE_SPAWN_LIMIT.get();
      waterSpawnLimit = WATER_SPAWN_LIMIT.get();
      lavaSpawnLimit = LAVA_SPAWN_LIMIT.get();
      vanillaMonsterSpawnLimit = VANILLA_MONSTER_SPAWN_LIMIT.get();
      vanillaAmbientSpawnLimit = VANILLA_AMBIENT_SPAWN_LIMIT.get();
      vanillaCreatureSpawnLimit = VANILLA_CREATURE_SPAWN_LIMIT.get();
      vanillaWaterSpawnLimit = VANILLA_WATER_SPAWN_LIMIT.get();
      otherSpawnLimit = OTHER_SPAWN_LIMIT.get();

      List<? extends Integer> dimList = DIMENSION_WHITELIST.get();
      dimensionWhiteList = new int[dimList.size()];
      for (int i = 0; i < dimList.size(); i++) {
         dimensionWhiteList[i] = dimList.get(i);
      }
   }

   public static void load() {
      loadGeneralOptions();
      if (defaultEntitiesToSpawn.isEmpty()) {
         defaultEntitiesToSpawn.add("");
      }

      List<? extends String> entityList = ENTITIES_TO_SPAWN.get();
      entitiesToSpawn = entityList.toArray(new String[0]);
   }

   public static String[] getConfigSpawnEntries() {
      return entitiesToSpawn;
   }

   public static int getSpawnTickDelay() {
      return spawnTickDelay;
   }

   public static int getSpawnLimitIncrease(MobCategory type) {
      if (type == MobCategory.MONSTER) {
         return vanillaMonsterSpawnLimit;
      } else if (type == MobCategory.AMBIENT) {
         return vanillaAmbientSpawnLimit;
      } else if (type == MobCategory.CREATURE) {
         return vanillaCreatureSpawnLimit;
      } else if (type == MobCategory.WATER_CREATURE) {
         return vanillaWaterSpawnLimit;
      } else if (type == MultiMob.MULTIMOB_MONSTER) {
         return monsterSpawnLimit;
      } else if (type == MultiMob.MULTIMOB_PASSIVE) {
         return passiveSpawnLimit;
      } else if (type == MultiMob.MULTIMOB_WATER) {
         return waterSpawnLimit;
      } else {
         return type == MultiMob.MULTIMOB_LAVA ? lavaSpawnLimit : otherSpawnLimit;
      }
   }

   public static boolean getSpawnInSpectate() {
      return spawnInSpectate;
   }

   public static boolean getUseAdditionalSpawning() {
      return useAdditionalSpawning;
   }

   public static void addConfigSpawnEntry(MMConfigSpawnEntry entry) {
      MMConfig.EXTERNALCONFIGSPAWNS.add(entry);
   }

   public static int[] getDimensionWhiteList() {
      return dimensionWhiteList;
   }

   public static String[] convertListToArray(List<String> list) {
      String[] array = new String[list.size()];

      for(int i = 0; i < array.length; ++i) {
         array[i] = (String)list.get(i);
      }

      return array;
   }
}
