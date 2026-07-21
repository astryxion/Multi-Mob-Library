package net.daveyx0.multimob.spawn;

import java.util.List;
import java.util.Set;
import net.daveyx0.multimob.core.MMEntityRegistry;
import net.daveyx0.multimob.core.MultiMob;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.ForgeRegistries;

public class MMSpawnChecks {
   private static final int distanceCheck = 10;
   private static boolean enableDebug = false;

   public static boolean performSpawnChecks(ServerLevel worldIn, BlockPos pos, MMSpawnEntry entry) {
      if (!entry.getIsAllowedToSpawn()) {
         debug(entry, 0, pos, enableDebug);
         return false;
      } else if (!isWithinWorldBorder(worldIn, pos)) {
         debug(entry, 1, pos, enableDebug);
         return false;
      } else if (!entry.getIsAllowedOnPeaceful() && worldIn.getDifficulty() == Difficulty.PEACEFUL) {
         debug(entry, 2, pos, enableDebug);
         return false;
      } else if (!isLuckyEnoughToSpawn(worldIn, entry.getAdditionalRarity())) {
         debug(entry, 3, pos, enableDebug);
         return false;
      } else if (!isDimensionSuitable(worldIn, entry.getDimensions())) {
         debug(entry, 4, pos, enableDebug);
         return false;
      } else if (!isWeatherConditionSuitable(worldIn, pos, entry.getWeatherCondition())) {
         debug(entry, 19, pos, enableDebug);
         return false;
      } else if (!isInsideSuitableStructure(worldIn, pos, entry.getStructures())) {
         debug(entry, 7, pos, enableDebug);
         return false;
      } else if (entry.getNeedsToSeeSky() && !canPositionSeeSky(worldIn, pos)) {
         debug(entry, 18, pos, enableDebug);
         return false;
      } else if (!isHeightLevelSuitable(pos, entry.getHeightLevelRange()[0], entry.getHeightLevelRange()[1])) {
         debug(entry, 9, pos, enableDebug);
         return false;
      } else if (!isLightLevelSuitable(worldIn, pos, entry.getLightLevelRange()[0], entry.getLightLevelRange()[1])) {
         debug(entry, 10, pos, enableDebug);
         return false;
      } else if (entry.getNeedsMoreSpace() && !hasLoadsOfSpaceAbove(worldIn, pos)) {
         debug(entry, 11, pos, enableDebug);
         return false;
      } else if (!isNearEntity(worldIn, pos, entry.getEntitiesNearList(), (double)10.0F)) {
         debug(entry, 12, pos, enableDebug);
         return false;
      } else if (!isNearBlock(worldIn, pos, entry.getBlocksNearList(), 10)) {
         debug(entry, 13, pos, enableDebug);
         return false;
      } else {
         return true;
      }
   }

   public static boolean canEntitySpawnHere(Entity entity, MMSpawnEntry entry) {
      if (entity != null && entity instanceof Mob && (!MMEntityRegistry.entities.containsKey(entity.getClass()) || (Boolean)MMEntityRegistry.entities.get(entity.getClass()))) {
         BlockPos pos = entity.blockPosition();
         Mob entityLiving = (Mob)entity;
         if (!entityLiving.checkSpawnObstruction(entity.level())) {
            debug(entry, 14, pos, enableDebug);
            return false;
         } else if (!entry.getOverrideCanGetSpawnHere()) {
            return entityLiving.checkSpawnRules(entity.level(), net.minecraft.world.entity.MobSpawnType.NATURAL);
         } else if (!canSpawnOnBlock(entity, entry.getSpawnBlocksList())) {
            debug(entry, 15, pos, enableDebug);
            return false;
         } else if (!checkCanEntitySpawnOnBlockState(entity)) {
            debug(entry, 16, pos, enableDebug);
            return false;
         } else if (entity instanceof PathfinderMob && !checkBlockPathWeight((PathfinderMob)entity)) {
            debug(entry, 17, pos, enableDebug);
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean isInsideSuitableStructure(ServerLevel worldIn, BlockPos pos, List<String> structures) {
      if (structures != null && !structures.isEmpty()) {
         for(String entry : structures) {
            TagKey<net.minecraft.world.level.levelgen.structure.Structure> structureTag = TagKey.create(Registries.STRUCTURE, new ResourceLocation(entry.toLowerCase()));
            if (worldIn.structureManager().getStructureWithPieceAt(pos, structureTag).isValid()) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public static boolean isWeatherConditionSuitable(Level worldIn, BlockPos pos, MMSpawnEntry.WeatherCondition condition) {
      if (condition == MMSpawnEntry.WeatherCondition.NONE) {
         return true;
      } else if (condition == MMSpawnEntry.WeatherCondition.GENERAL_DOWNFALL) {
         return worldIn.isRaining();
      } else if (condition == MMSpawnEntry.WeatherCondition.THUNDERSTORM) {
         return worldIn.isThundering();
      } else {
         return condition != MMSpawnEntry.WeatherCondition.RAIN && condition != MMSpawnEntry.WeatherCondition.SNOW ? false : isDownfallAtPosition(worldIn, pos, condition);
      }
   }

   public static boolean isWithinWorldBorder(Level worldIn, BlockPos pos) {
      return worldIn.getWorldBorder().isWithinBounds(pos);
   }

   public static boolean isHeightLevelSuitable(BlockPos pos, int min, int max) {
      if (min <= -1 && max <= -1) {
         return true;
      } else if (min <= -1) {
         return pos.getY() <= max;
      } else if (max <= -1) {
         return pos.getY() >= min;
      } else {
         return pos.getY() >= min && pos.getY() <= max;
      }
   }

   public static boolean isLightLevelSuitable(Level worldIn, BlockPos pos, int min, int max) {
      if (min == -1 && max == -1) {
         return true;
      } else if (min == -2 && max == -2) {
         return isValidMobLightLevel(worldIn, pos);
      } else if (min <= -1) {
         return worldIn.getMaxLocalRawBrightness(pos) <= max;
      } else if (max <= -1) {
         return worldIn.getMaxLocalRawBrightness(pos) >= min;
      } else {
         return worldIn.getMaxLocalRawBrightness(pos) >= min && worldIn.getMaxLocalRawBrightness(pos) <= max;
      }
   }

   public static boolean isNearEntity(Level worldIn, BlockPos pos, List<EntityType<?>> entities, double distance) {
      if (entities != null && !entities.isEmpty() && !(distance <= (double)-1.0F)) {
         List<EntityType<?>> remaining = new java.util.ArrayList<>(entities);
         for(Entity entity : worldIn.getEntities(null, new net.minecraft.world.phys.AABB(pos).inflate(distance))) {
            if (entity != null && remaining.contains(entity.getType())) {
               double d0 = entity.distanceToSqr((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
               if (d0 < (double)0.0F || d0 < distance * distance) {
                  remaining.remove(entity.getType());
               }
            }
         }

         return remaining.isEmpty();
      } else {
         return true;
      }
   }

   public static boolean isNearBlock(Level worldIn, BlockPos pos, List<BlockState> blockstates, int searchLength) {
      if (blockstates != null && !blockstates.isEmpty() && searchLength > -1) {
         List<BlockState> remaining = new java.util.ArrayList<>(blockstates);
         int i = searchLength;
         int j = 1;

         for(int k = 0; k <= 1; k = k > 0 ? -k : 1 - k) {
            for(int l = 0; l < i; ++l) {
               for(int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                  for(int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                     BlockPos blockpos1 = pos.offset(i1, k, j1);
                     if (remaining.contains(worldIn.getBlockState(blockpos1))) {
                        remaining.remove(worldIn.getBlockState(blockpos1));
                     }
                  }
               }
            }
         }

         return remaining.isEmpty();
      } else {
         return true;
      }
   }

   public static boolean isLuckyEnoughToSpawn(Level worldIn, int chance) {
      // 0 and negative disable the rarity gate. nextInt(0) throws and has crashed config tweaks.
      if (chance <= 0) {
         return true;
      } else {
         return worldIn.random.nextInt(chance) == 0;
      }
   }

   public static boolean isBiomeSuitable(Level worldIn, BlockPos pos, List<Biome> biomes) {
      return biomes != null && !biomes.isEmpty() ? biomes.contains(worldIn.getBiome(pos).value()) : true;
   }

   public static boolean isBiomeTypeSuitable(Level worldIn, BlockPos pos, List<TagKey<Biome>> types) {
      if (types != null && !types.isEmpty()) {
         Holder<Biome> biomeHolder = worldIn.getBiome(pos);

         for(TagKey<Biome> type : types) {
            if (!biomeHolder.is(type)) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isDimensionSuitable(Level worldIn, List<Integer> ids) {
      if (ids != null && !ids.isEmpty()) {
         int dimId = getDimensionId(worldIn);
         return ids.contains(dimId);
      } else {
         return getDimensionId(worldIn) == 0;
      }
   }

   private static int getDimensionId(Level worldIn) {
      if (worldIn.dimension() == Level.OVERWORLD) return 0;
      if (worldIn.dimension() == Level.NETHER) return -1;
      if (worldIn.dimension() == Level.END) return 1;
      return 0;
   }

   public static boolean isAllowedToSpawnOnPeaceful(Level worldIn, boolean check) {
      return check ? true : worldIn.getDifficulty() != Difficulty.PEACEFUL;
   }

   public static boolean canCreatureTypeSpawnHere(Level worldIn, BlockPos pos, SpawnPlacements.Type spawnType, String spawnTypeString) {
      BlockState iblockstate = worldIn.getBlockState(pos);
      if (spawnTypeString != null && spawnTypeString.equals("LAVA")) {
         FluidState fluidState = worldIn.getFluidState(pos);
         FluidState fluidBelow = worldIn.getFluidState(pos.below());
         return fluidState.getType() == Fluids.LAVA && fluidBelow.getType() == Fluids.LAVA && !worldIn.getBlockState(pos.above()).isCollisionShapeFullBlock(worldIn, pos.above());
      } else if (spawnType == SpawnPlacements.Type.IN_WATER) {
         FluidState fluidState = worldIn.getFluidState(pos);
         FluidState fluidBelow = worldIn.getFluidState(pos.below());
         return fluidState.getType() == Fluids.WATER && fluidBelow.getType() == Fluids.WATER && !worldIn.getBlockState(pos.above()).isCollisionShapeFullBlock(worldIn, pos.above());
      } else {
         BlockPos blockpos = pos.below();
         BlockState state = worldIn.getBlockState(blockpos);
         if (!state.isValidSpawn(worldIn, blockpos, null)) {
            return false;
         } else {
            Block block = worldIn.getBlockState(blockpos).getBlock();
            boolean flag = block != Blocks.BEDROCK && block != Blocks.BARRIER;
            return flag && NaturalSpawner.isValidEmptySpawnBlock(worldIn, pos, worldIn.getBlockState(pos), worldIn.getFluidState(pos), null) && NaturalSpawner.isValidEmptySpawnBlock(worldIn, pos.above(), worldIn.getBlockState(pos.above()), worldIn.getFluidState(pos.above()), null);
         }
      }
   }

   public static boolean hasLoadsOfSpaceAbove(Level worldIn, BlockPos pos) {
      return check3x3IsAirBlock(worldIn, pos.above()) && check3x3IsAirBlock(worldIn, pos.above(2));
   }

   public static boolean check3x3IsAirBlock(Level worldIn, BlockPos pos) {
      return worldIn.isEmptyBlock(pos) && worldIn.isEmptyBlock(pos.west()) && worldIn.isEmptyBlock(pos.west().north()) && worldIn.isEmptyBlock(pos.west().south()) && worldIn.isEmptyBlock(pos.east()) && worldIn.isEmptyBlock(pos.east().north()) && worldIn.isEmptyBlock(pos.east().south()) && worldIn.isEmptyBlock(pos.north()) && worldIn.isEmptyBlock(pos.south());
   }

   protected static boolean isValidMobLightLevel(Level worldIn, BlockPos pos) {
      if (worldIn.getBrightness(LightLayer.SKY, pos) > worldIn.random.nextInt(32)) {
         return false;
      } else {
         int i = worldIn.getMaxLocalRawBrightness(pos);
         if (worldIn.isThundering()) {
            int j = worldIn.getSkyDarken();
            worldIn.setSkyFlashTime(10);
            i = worldIn.getMaxLocalRawBrightness(pos);
            worldIn.setSkyFlashTime(j);
         }

         return i <= worldIn.random.nextInt(8);
      }
   }

   public static boolean canPositionSeeSky(Level worldIn, BlockPos pos) {
      return worldIn.canSeeSky(pos);
   }

   public static boolean checkCanEntitySpawnOnBlockState(Entity entity) {
      BlockState iblockstate = entity.level().getBlockState((new BlockPos(entity.blockPosition())).below());
      return iblockstate.isValidSpawn(entity.level(), entity.blockPosition().below(), entity.getType());
   }

   public static boolean checkBlockPathWeight(PathfinderMob creature) {
      return creature.getWalkTargetValue(new BlockPos(Mth.floor(creature.getX()), Mth.floor(creature.getBoundingBox().minY), Mth.floor(creature.getZ()))) >= 0.0F;
   }

   public static boolean canSpawnOnBlock(Entity entity, List<BlockState> blockstates) {
      if (blockstates != null && !blockstates.isEmpty()) {
         int i = Mth.floor(entity.getX());
         int j = Mth.floor(entity.getBoundingBox().minY);
         int k = Mth.floor(entity.getZ());
         BlockPos blockpos = new BlockPos(i, j, k);
         return blockstates.contains(entity.level().getBlockState(blockpos.below()));
      } else {
         return true;
      }
   }

   public static boolean isDownfallAtPosition(Level worldIn, BlockPos position, MMSpawnEntry.WeatherCondition condition) {
      if (!worldIn.isRaining()) {
         return false;
      } else if (!worldIn.canSeeSky(position)) {
         return false;
      } else if (worldIn.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, position).getY() > position.getY()) {
         return false;
      } else {
         Biome biome = worldIn.getBiome(position).value();
         if (condition == MMSpawnEntry.WeatherCondition.RAIN) {
            return biome.getPrecipitationAt(position) == Biome.Precipitation.RAIN;
         } else if (condition != MMSpawnEntry.WeatherCondition.SNOW) {
            return true;
         } else {
            return biome.getPrecipitationAt(position) == Biome.Precipitation.SNOW;
         }
      }
   }

   private static void debug(MMSpawnEntry entry, int context, BlockPos pos, boolean allow) {
      if (allow) {
         String message = entry.getEntryName() + " failed to spawn at " + pos.toString() + " due to ";
         switch (context) {
            case 0:
               message = message + "the entry not being allowed to spawn.";
               break;
            case 1:
               message = message + "the position being outside of the world border.";
               break;
            case 2:
               message = message + "the entry not being allowed to spawn on Peaceful.";
               break;
            case 3:
               message = message + "the entry not being lucky enough to spawn.";
               break;
            case 4:
               message = message + "the dimension not being suitable.";
               break;
            case 5:
               message = message + "the biome type not being suitable.";
               break;
            case 6:
               message = message + "the biome not being suitable.";
               break;
            case 7:
               message = message + "the structure not being at position or suitable.";
               break;
            case 8:
               message = message + "the spawn placement type preventing spawn.";
               break;
            case 9:
               message = message + "the height position not being suitable.";
               break;
            case 10:
               message = message + "the light level at position not being suitable.";
               break;
            case 11:
               message = message + "there not being enough available space.";
               break;
            case 12:
               message = message + "the appropriate entity not being nearby.";
               break;
            case 13:
               message = message + "the appropriate block not being nearby.";
               break;
            case 14:
               message = message + "the entity colliding.";
               break;
            case 15:
               message = message + "the entity not being on the correct block.";
               break;
            case 16:
               message = message + "the blockstate below not allowing entities to spawn.";
               break;
            case 17:
               message = message + "the block path weight being too low.";
               break;
            case 18:
               message = message + "the position could not see the sky.";
               break;
            case 19:
               message = message + "the position does not have a suitable weather condition.";
         }

         MultiMob.LOGGER.info(message);
      }
   }
}
