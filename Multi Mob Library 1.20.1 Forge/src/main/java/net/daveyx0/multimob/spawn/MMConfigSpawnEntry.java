package net.daveyx0.multimob.spawn;

import java.util.ArrayList;
import java.util.List;
import net.daveyx0.multimob.core.MultiMob;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.ForgeRegistries;

public class MMConfigSpawnEntry {
   private static String category1 = "spawnLimit";
   private static String category2 = "spawnAllowed";
   private static String category3 = "peacefulAllowed";
   private static String category4 = "addedRarity";
   private static String category5 = "spawnBiomeTypes";
   private static String category6 = "spawnBiomes";
   private static String category7 = "spawnDimensions";
   private static String category8 = "heightLevels";
   private static String category9 = "lightLevels";
   private static String category10 = "spawnType";
   private static String category11 = "weatherCondition";
   private static String category12 = "spawnBlocks";
   private static String category13 = "blocksNearSpawn";
   private static String category14 = "spawnStructures";
   private static String category15 = "entitiesNearSpawn";
   private static String category16 = "needsMoreSpace";
   private static String category17 = "needsToSeeSky";
   private static String category18 = "canOverrideSpawnChecks";
   private static String category19 = "creatureTypes";
   private static String category20 = "spawnWeights";
   private static String category21 = "spawnGroupSizes";
   private static String category22 = "spawnVariants";
   private String entryName;
   public String entityName;
   private final int defaultSpawnWeight;
   private final boolean defaultIsAllowedToSpawn;
   private String spawnType;
   private String weatherCondition;
   private String creatureType;
   private String[] entitiesNear;
   private String[] spawnBlocks;
   private String[] blocksNear;
   private String[] biomes;
   private String[] biomeTypes;
   private String[] structures;
   private int[] dimensions;
   private int[] heightLevels;
   private int[] lightLevels;
   private int[] groupSize;
   private boolean allowedOnPeaceful;
   private boolean overrideEntityCanSpawnHere;
   private boolean isAllowedToSpawn;
   private boolean needsLoadsOfSpace;
   private boolean needsToSeeSky;
   private int spawnFrequency;
   private int additionalRarity;
   private int spawnWeight;
   private int variantID;
   private String defaultSpawnType;
   private String defaultWeatherType;
   private String defaultCreatureType;
   private String[] defaultEntitiesNear;
   private String[] defaultSpawnBlocks;
   private String[] defaultBlocksNear;
   private String[] defaultBiomes;
   private String[] defaultBiomeTypes;
   private String[] defaultStructures;
   private int[] defaultDimensions;
   private int[] defaultHeightLevels;
   private int[] defaultLightLevels;
   private int[] defaultGroupSize;
   private boolean defaultAllowedOnPeaceful;
   private boolean defaultOverrideEntityCanSpawnHere;
   private boolean defaultNeedsLoadsOfSpace;
   private boolean defaultNeedsToSeeSky;
   private int defaultAdditionalRarity;
   private int defaultSpawnFrequency;
   private int defaultVariantID;

   public MMConfigSpawnEntry(String entryName, String entityName) {
      this(entryName, entityName, 100, true);
      this.entryName = entryName;
      this.entityName = entityName;
   }

   public MMConfigSpawnEntry(String entryName, String entityName, int spawnWeight, boolean isAllowedToSpawn) {
      this.defaultSpawnType = "GROUND";
      this.defaultWeatherType = "NONE";
      this.defaultCreatureType = "MULTIMOBMONSTER";
      this.defaultEntitiesNear = new String[]{""};
      this.defaultSpawnBlocks = new String[]{""};
      this.defaultBlocksNear = new String[]{""};
      this.defaultBiomes = new String[]{""};
      this.defaultBiomeTypes = new String[]{""};
      this.defaultStructures = new String[]{""};
      this.defaultDimensions = new int[]{0};
      this.defaultHeightLevels = new int[]{-1, -1};
      this.defaultLightLevels = new int[]{-1, -1};
      this.defaultGroupSize = new int[]{1, 1};
      this.defaultAllowedOnPeaceful = false;
      this.defaultOverrideEntityCanSpawnHere = false;
      this.defaultNeedsLoadsOfSpace = false;
      this.defaultNeedsToSeeSky = false;
      this.defaultAdditionalRarity = -1;
      this.defaultSpawnFrequency = -1;
      this.defaultVariantID = -1;
      this.entryName = entryName;
      this.entityName = entityName;
      this.defaultSpawnWeight = spawnWeight;
      this.defaultIsAllowedToSpawn = isAllowedToSpawn;
      this.spawnFrequency = this.defaultSpawnFrequency;
      this.isAllowedToSpawn = this.defaultIsAllowedToSpawn;
      this.allowedOnPeaceful = this.defaultAllowedOnPeaceful;
      this.additionalRarity = this.defaultAdditionalRarity;
      this.biomeTypes = this.defaultBiomeTypes;
      this.biomes = this.defaultBiomes;
      this.dimensions = this.defaultDimensions;
      this.heightLevels = this.defaultHeightLevels;
      this.lightLevels = this.defaultLightLevels;
      this.spawnType = this.defaultSpawnType;
      this.weatherCondition = this.defaultWeatherType;
      this.spawnBlocks = this.defaultSpawnBlocks;
      this.blocksNear = this.defaultBlocksNear;
      this.structures = this.defaultStructures;
      this.entitiesNear = this.defaultEntitiesNear;
      this.needsLoadsOfSpace = this.defaultNeedsLoadsOfSpace;
      this.needsToSeeSky = this.defaultNeedsToSeeSky;
      this.overrideEntityCanSpawnHere = this.defaultOverrideEntityCanSpawnHere;
      this.creatureType = this.defaultCreatureType;
      this.spawnWeight = this.defaultSpawnWeight;
      this.groupSize = this.defaultGroupSize;
      this.variantID = this.defaultVariantID;
   }

   public void load() {
      this.spawnFrequency = this.defaultSpawnFrequency;
      this.isAllowedToSpawn = this.defaultIsAllowedToSpawn;
      this.allowedOnPeaceful = this.defaultAllowedOnPeaceful;
      this.additionalRarity = this.defaultAdditionalRarity;
      this.biomeTypes = this.defaultBiomeTypes;
      this.biomes = this.defaultBiomes;
      this.dimensions = this.defaultDimensions;
      this.heightLevels = this.defaultHeightLevels;
      this.lightLevels = this.defaultLightLevels;
      this.spawnType = this.defaultSpawnType;
      this.weatherCondition = this.defaultWeatherType;
      this.spawnBlocks = this.defaultSpawnBlocks;
      this.blocksNear = this.defaultBlocksNear;
      this.structures = this.defaultStructures;
      this.entitiesNear = this.defaultEntitiesNear;
      this.needsLoadsOfSpace = this.defaultNeedsLoadsOfSpace;
      this.needsToSeeSky = this.defaultNeedsToSeeSky;
      this.overrideEntityCanSpawnHere = this.defaultOverrideEntityCanSpawnHere;
      this.creatureType = this.defaultCreatureType;
      this.spawnWeight = this.defaultSpawnWeight;
      this.groupSize = this.defaultGroupSize;
      this.variantID = this.defaultVariantID;
   }

   public String getEntryName() {
      return this.entryName;
   }

   public SpawnPlacements.Type getSpawnPlacementType() {
      if (this.spawnType.equals("LAVA")) {
         return SpawnPlacements.Type.ON_GROUND;
      } else if (this.spawnType.equals("AIR")) {
         return SpawnPlacements.Type.NO_RESTRICTIONS;
      } else {
         return this.spawnType.equals("WATER") ? SpawnPlacements.Type.IN_WATER : SpawnPlacements.Type.ON_GROUND;
      }
   }

   public String getSpawnTypeString() {
      return this.spawnType;
   }

   public MMSpawnEntry.WeatherCondition getWeatherCondition() {
      if (this.weatherCondition.equals("DOWNFALL")) {
         return MMSpawnEntry.WeatherCondition.GENERAL_DOWNFALL;
      } else if (this.weatherCondition.equals("THUNDER")) {
         return MMSpawnEntry.WeatherCondition.THUNDERSTORM;
      } else if (this.weatherCondition.equals("RAIN")) {
         return MMSpawnEntry.WeatherCondition.RAIN;
      } else {
         return this.weatherCondition.equals("SNOW") ? MMSpawnEntry.WeatherCondition.SNOW : MMSpawnEntry.WeatherCondition.NONE;
      }
   }

   public List<EntityType<?>> getEntitiesNearList() {
      if (this.entitiesNear != null && !this.entitiesNear[0].equals("")) {
         List<EntityType<?>> entryList = new ArrayList();

         for(String entry : this.entitiesNear) {
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entry));
            if (entityType != null) {
               entryList.add(entityType);
            }
         }

         return entryList;
      } else {
         return null;
      }
   }

   public List<Biome> getBiomeList() {
      if (this.biomes != null && !this.biomes[0].equals("")) {
         List<Biome> entryList = new ArrayList();

         for(String entry : this.biomes) {
            Biome biomeEntry = ForgeRegistries.BIOMES.getValue(new ResourceLocation(entry));
            if (biomeEntry != null) {
               entryList.add(biomeEntry);
            }
         }

         return entryList;
      } else {
         return null;
      }
   }

   public List<TagKey<Biome>> getBiomeTypeList() {
      if (this.biomeTypes != null && !this.biomeTypes[0].equals("")) {
         List<TagKey<Biome>> entryList = new ArrayList();

         for(String entry : this.biomeTypes) {
            TagKey<Biome> biomeTypeEntry = TagKey.create(Registries.BIOME, new ResourceLocation(entry.toLowerCase()));
            if (biomeTypeEntry != null) {
               entryList.add(biomeTypeEntry);
            }
         }

         return entryList;
      } else {
         return null;
      }
   }

   public int[] getHeightLevelRange() {
      return this.heightLevels;
   }

   public int[] getLightLevelRange() {
      return this.lightLevels;
   }

   public List<BlockState> getBlocksNearList() {
      return this.blocksNear != null && !this.blocksNear[0].equals("") ? this.createBlockStateList(this.blocksNear) : null;
   }

   public List<BlockState> getSpawnBlockList() {
      return this.spawnBlocks != null && !this.spawnBlocks[0].equals("") ? this.createBlockStateList(this.spawnBlocks) : null;
   }

   public List<BlockState> createBlockStateList(String[] array) {
      List<BlockState> entryList = new ArrayList();

      for(String entry : array) {
         Block blockEntry = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(entry));
         if (blockEntry != null) {
            entryList.add(blockEntry.defaultBlockState());
         } else {
            for(Block block : ForgeRegistries.BLOCKS) {
               for(BlockState state : block.getStateDefinition().getPossibleStates()) {
                  if (entry.equals(state.toString())) {
                     entryList.add(state);
                  }
               }
            }
         }
      }

      return entryList;
   }

   public List<Integer> getDimensionIdList() {
      if (this.dimensions == null) {
         return null;
      } else {
         List<Integer> entryList = new ArrayList();

         for(int entry : this.dimensions) {
            entryList.add(entry);
         }

         return entryList;
      }
   }

   public boolean getAllowedOnPeaceful() {
      return this.allowedOnPeaceful;
   }

   public boolean getOverridesEntityCanSpawnHere() {
      return this.overrideEntityCanSpawnHere;
   }

   public boolean getIsAllowedToSpawn() {
      return this.isAllowedToSpawn;
   }

   public boolean getNeedsLoadsOfSpace() {
      return this.needsLoadsOfSpace;
   }

   public boolean getNeedsToSeeSky() {
      return this.needsToSeeSky;
   }

   public int getSpawnLimit() {
      return this.spawnFrequency;
   }

   public int getAdditionalRarity() {
      return this.additionalRarity;
   }

   public int getVariantID() {
      return this.variantID;
   }

   public int getSpawnWeight() {
      return this.spawnWeight;
   }

   public MobCategory getCreatureType() {
      if (this.creatureType.equals("MULTIMOBMONSTER")) {
         return MultiMob.MULTIMOB_MONSTER;
      } else if (this.creatureType.equals("MULTIMOBPASSIVE")) {
         return MultiMob.MULTIMOB_PASSIVE;
      } else if (this.creatureType.equals("MULTIMOBWATER")) {
         return MultiMob.MULTIMOB_WATER;
      } else if (this.creatureType.equals("MULTIMOBLAVA")) {
         return MultiMob.MULTIMOB_LAVA;
      } else if (this.creatureType.equals("CREATURE")) {
         return MobCategory.CREATURE;
      } else if (this.creatureType.equals("AMBIENT")) {
         return MobCategory.AMBIENT;
      } else {
         return this.creatureType.equals("WATERCREATURE") ? MobCategory.WATER_CREATURE : MobCategory.MONSTER;
      }
   }

   public int[] getGroupSizeRange() {
      return this.groupSize;
   }

   public List<String> getStructureList() {
      if (this.structures != null && !this.structures[0].equals("")) {
         List<String> entryList = new ArrayList();

         for(String entry : this.structures) {
            entryList.add(entry);
         }

         return entryList;
      } else {
         return null;
      }
   }

   public MMConfigSpawnEntry setSpawnType(String i) {
      this.defaultSpawnType = i;
      return this;
   }

   public MMConfigSpawnEntry setWeatherCondition(String i) {
      this.defaultWeatherType = i;
      return this;
   }

   public MMConfigSpawnEntry setEntitiesNear(String[] i) {
      this.defaultEntitiesNear = i;
      return this;
   }

   public MMConfigSpawnEntry setSpawnBlocks(String[] i) {
      this.defaultSpawnBlocks = i;
      return this;
   }

   public MMConfigSpawnEntry setBlocksNear(String[] i) {
      this.defaultBlocksNear = i;
      return this;
   }

   public MMConfigSpawnEntry setBiomes(String[] i) {
      this.defaultBiomes = i;
      return this;
   }

   public MMConfigSpawnEntry setBiomeTypes(String[] i) {
      this.defaultBiomeTypes = i;
      return this;
   }

   public MMConfigSpawnEntry setStructures(String[] i) {
      this.defaultStructures = i;
      return this;
   }

   public MMConfigSpawnEntry setDimensions(int[] i) {
      this.defaultDimensions = i;
      return this;
   }

   public MMConfigSpawnEntry setAllowedOnPeacful(boolean i) {
      this.defaultAllowedOnPeaceful = i;
      return this;
   }

   public MMConfigSpawnEntry setOverrideCanSpawnHere(boolean i) {
      this.defaultOverrideEntityCanSpawnHere = i;
      return this;
   }

   public MMConfigSpawnEntry setNeedsMoreSpace(boolean i) {
      this.defaultNeedsLoadsOfSpace = i;
      return this;
   }

   public MMConfigSpawnEntry setNeedsToSeeSky(boolean i) {
      this.defaultNeedsToSeeSky = i;
      return this;
   }

   public MMConfigSpawnEntry setAdditionalRarity(int i) {
      this.defaultAdditionalRarity = i;
      return this;
   }

   public MMConfigSpawnEntry setHeightLevel(int i, int j) {
      this.defaultHeightLevels[0] = i;
      this.defaultHeightLevels[1] = j;
      return this;
   }

   public MMConfigSpawnEntry setLightLevel(int i, int j) {
      this.defaultLightLevels[0] = i;
      this.defaultLightLevels[1] = j;
      return this;
   }

   public MMConfigSpawnEntry setSpawnLimit(int i) {
      this.defaultSpawnFrequency = i;
      return this;
   }

   public MMConfigSpawnEntry setCreatureType(String i) {
      this.defaultCreatureType = i;
      return this;
   }

   public MMConfigSpawnEntry setGroupSize(int i, int j) {
      this.defaultGroupSize[0] = i;
      this.defaultGroupSize[1] = j;
      return this;
   }

   public MMConfigSpawnEntry setVariantID(int i) {
      this.defaultVariantID = i;
      return this;
   }

   public MMConfigSpawnEntry setupBaseAnimalSpawnEntry(boolean overrideSpawnChecks) {
      this.defaultSpawnBlocks = new String[]{"minecraft:grass_block"};
      this.defaultLightLevels[0] = 9;
      this.defaultAllowedOnPeaceful = true;
      this.overrideEntityCanSpawnHere = overrideSpawnChecks;
      return this;
   }

   public MMConfigSpawnEntry setupBaseMobSpawnEntry(boolean overrideSpawnChecks) {
      this.defaultLightLevels[0] = -2;
      this.defaultLightLevels[1] = -2;
      this.overrideEntityCanSpawnHere = overrideSpawnChecks;
      return this;
   }
}
