package net.daveyx0.multimob.spawn;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;

public class MMSpawnEntry {
   private String entryName;
   private EntityType<?> entityType;
   private SpawnPlacementType spawnType;
   private String spawnTypeString;
   private WeatherCondition weatherCondition;
   private MobCategory creatureType;
   private int[] heightLevelRange;
   private int[] lightLevelRange;
   private int[] groupSizeRange;
   private List<EntityType<?>> entitiesNear;
   private List<BlockState> blocksNear;
   private List<BlockState> spawnBlocks;
   private List<ResourceLocation> biomes;
   private List<TagKey<Biome>> biomeTypes;
   private List<String> structures;
   private List<Integer> dimensions;
   private boolean allowedOnPeaceful;
   private boolean overrideEntityCanSpawnHere;
   private boolean isAllowedToSpawn;
   private boolean needsLoadsOfSpace;
   private boolean needsToSeeSky;
   private int spawnFrequency;
   private int additionalRarity;
   private int spawnWeight;
   private int variantID;

   public MMSpawnEntry(String entryName, EntityType<?> entityType, MMConfigSpawnEntry config) {
      this.entryName = entryName;
      this.entityType = entityType;
      this.reloadInfoFromConfig(config);
   }

   public String toString() {
      return this.entryName + " " + this.entityType.toString();
   }

   public void reloadInfoFromConfig(MMConfigSpawnEntry config) {
      this.spawnType = config.getSpawnPlacementType();
      this.spawnTypeString = config.getSpawnTypeString();
      this.heightLevelRange = config.getHeightLevelRange();
      this.lightLevelRange = config.getLightLevelRange();
      this.entitiesNear = config.getEntitiesNearList();
      this.blocksNear = config.getBlocksNearList();
      this.spawnBlocks = config.getSpawnBlockList();
      this.biomes = config.getBiomeList();
      this.biomeTypes = config.getBiomeTypeList();
      this.structures = config.getStructureList();
      this.dimensions = config.getDimensionIdList();
      this.allowedOnPeaceful = config.getAllowedOnPeaceful();
      this.overrideEntityCanSpawnHere = config.getOverridesEntityCanSpawnHere();
      this.isAllowedToSpawn = config.getIsAllowedToSpawn();
      this.needsLoadsOfSpace = config.getNeedsLoadsOfSpace();
      this.needsToSeeSky = config.getNeedsToSeeSky();
      this.spawnFrequency = config.getSpawnLimit();
      this.additionalRarity = config.getAdditionalRarity();
      this.weatherCondition = config.getWeatherCondition();
      this.spawnWeight = config.getSpawnWeight();
      this.creatureType = config.getCreatureType();
      this.groupSizeRange = config.getGroupSizeRange();
      this.variantID = config.getVariantID();
   }

   public String getEntryName() {
      return this.entryName;
   }

   public EntityType<?> getEntityType() {
      return this.entityType;
   }

   public int getSpawnLimit() {
      return this.spawnFrequency;
   }

   public int getSpawnWeight() {
      return this.spawnWeight;
   }

   public int getAdditionalRarity() {
      return this.additionalRarity;
   }

   public SpawnPlacementType getSpawnPlacementType() {
      return this.spawnType;
   }

   public String getSpawnTypeString() {
      return this.spawnTypeString;
   }

   public WeatherCondition getWeatherCondition() {
      return this.weatherCondition;
   }

   public MobCategory getCreatureType() {
      return this.creatureType;
   }

   public int[] getHeightLevelRange() {
      return this.heightLevelRange;
   }

   public int[] getLightLevelRange() {
      return this.lightLevelRange;
   }

   public int[] getGroupSizeRange() {
      return this.groupSizeRange;
   }

   public List<EntityType<?>> getEntitiesNearList() {
      return this.entitiesNear;
   }

   public List<BlockState> getSpawnBlocksList() {
      return this.spawnBlocks;
   }

   public List<BlockState> getBlocksNearList() {
      return this.blocksNear;
   }

   public List<ResourceLocation> getBiomes() {
      return this.biomes;
   }

   public List<TagKey<Biome>> getBiomeTypes() {
      return this.biomeTypes;
   }

   public List<Integer> getDimensions() {
      return this.dimensions;
   }

   public List<String> getStructures() {
      return this.structures;
   }

   public boolean getIsAllowedOnPeaceful() {
      return this.allowedOnPeaceful;
   }

   public boolean getOverrideCanGetSpawnHere() {
      return this.overrideEntityCanSpawnHere;
   }

   public boolean getIsAllowedToSpawn() {
      return this.isAllowedToSpawn;
   }

   public boolean getNeedsMoreSpace() {
      return this.needsLoadsOfSpace;
   }

   public boolean getNeedsToSeeSky() {
      return this.needsToSeeSky;
   }

   public int getVariantID() {
      return this.variantID;
   }

   static enum WeatherCondition {
      NONE,
      GENERAL_DOWNFALL,
      THUNDERSTORM,
      RAIN,
      SNOW;
   }
}
