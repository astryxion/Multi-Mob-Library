package net.daveyx0.multimob.spawn;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.daveyx0.multimob.config.MMConfigSpawns;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.bus.api.Event;

public class MMWorldSpawner {
   private static final int MOB_COUNT_DIV = (int)Math.pow((double)17.0F, (double)2.0F);
   private final Set<ChunkPos> eligibleChunksForSpawning = Sets.newHashSet();

   public int findChunksForSpawning(ServerLevel worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
      if (!spawnHostileMobs && !spawnPeacefulMobs) {
         return 0;
      } else {
         int[] dimensionWhiteList = MMConfigSpawns.getDimensionWhiteList();
         boolean check = false;
         if (dimensionWhiteList != null && dimensionWhiteList.length != 0) {
            int dimId = 0;
            if (worldServerIn.dimension() == Level.OVERWORLD) dimId = 0;
            else if (worldServerIn.dimension() == Level.NETHER) dimId = -1;
            else if (worldServerIn.dimension() == Level.END) dimId = 1;
            for(int i = 0; i < dimensionWhiteList.length; ++i) {
               if (dimensionWhiteList[i] == dimId) {
                  check = true;
               }
            }
         }

         if (!check) {
            return 0;
         } else {
            this.eligibleChunksForSpawning.clear();
            int i = 0;

            for(Player entityplayer : worldServerIn.players()) {
               if (!entityplayer.isSpectator()) {
                  int j = Mth.floor(entityplayer.getX() / (double)16.0F);
                  int k = Mth.floor(entityplayer.getZ() / (double)16.0F);
                  int l = 8;

                  for(int i1 = -8; i1 <= 8; ++i1) {
                     for(int j1 = -8; j1 <= 8; ++j1) {
                        boolean flag = i1 == -8 || i1 == 8 || j1 == -8 || j1 == 8;
                        ChunkPos chunkpos = new ChunkPos(i1 + j, j1 + k);
                        if (!this.eligibleChunksForSpawning.contains(chunkpos)) {
                           ++i;
                           if (!flag && worldServerIn.getWorldBorder().isWithinBounds(chunkpos)) {
                              LevelChunk chunk = worldServerIn.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);
                              if (chunk != null) {
                                 this.eligibleChunksForSpawning.add(chunkpos);
                              }
                           }
                        }
                     }
                  }
               }
            }

            int j4 = 0;
            BlockPos blockpos1 = worldServerIn.getSharedSpawnPos();
            Map<MobCategory, Integer> mobCounts = new EnumMap<>(MobCategory.class);
            for (MobCategory category : MobCategory.values()) {
               mobCounts.put(category, 0);
            }
            // Only count Mobs — skipping items/orbs/etc. cuts a large share of getAllEntities work.
            for (Entity entity : worldServerIn.getAllEntities()) {
               if (entity instanceof Mob) {
                  mobCounts.merge(entity.getType().getCategory(), 1, Integer::sum);
               }
            }

            for(MobCategory enumcreaturetype : MobCategory.values()) {
               if ((!enumcreaturetype.isFriendly() || spawnPeacefulMobs) && (enumcreaturetype.isFriendly() || spawnHostileMobs) && (!enumcreaturetype.isPersistent() || spawnOnSetTickRate)) {
                  int k4 = mobCounts.getOrDefault(enumcreaturetype, 0);
                  int l4 = (enumcreaturetype.getMaxInstancesPerChunk() + MMConfigSpawns.getSpawnLimitIncrease(enumcreaturetype)) * i / MOB_COUNT_DIV;
                  if (k4 <= l4) {
                     ArrayList<ChunkPos> shuffled = Lists.newArrayList(this.eligibleChunksForSpawning);
                     Collections.shuffle(shuffled);
                     BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                     label153:
                     for(ChunkPos chunkpos1 : shuffled) {
                        BlockPos blockpos = getRandomChunkPosition(worldServerIn, chunkpos1.x, chunkpos1.z);
                        int k1 = blockpos.getX();
                        int l1 = blockpos.getY();
                        int i2 = blockpos.getZ();
                        BlockState iblockstate = worldServerIn.getBlockState(blockpos);
                        if (!iblockstate.isCollisionShapeFullBlock(worldServerIn, blockpos)) {
                           int j2 = 0;

                           for(int k2 = 0; k2 < 3; ++k2) {
                              int l2 = k1;
                              int i3 = l1;
                              int j3 = i2;
                              int k3 = 6;
                              MobSpawnSettings.SpawnerData biome$spawnlistentry = null;
                              SpawnGroupData ientitylivingdata = null;
                              int l3 = Mth.ceil(Math.random() * (double)4.0F);

                              for(int i4 = 0; i4 < l3; ++i4) {
                                 l2 += worldServerIn.random.nextInt(6) - worldServerIn.random.nextInt(6);
                                 i3 += worldServerIn.random.nextInt(1) - worldServerIn.random.nextInt(1);
                                 j3 += worldServerIn.random.nextInt(6) - worldServerIn.random.nextInt(6);
                                 blockpos$mutableblockpos.set(l2, i3, j3);
                                 float f = (float)l2 + 0.5F;
                                 float f1 = (float)j3 + 0.5F;
                                 if (!worldServerIn.hasNearbyAlivePlayer((double)f, (double)i3, (double)f1, (double)24.0F) && blockpos1.distSqr(new BlockPos(Mth.floor(f), i3, Mth.floor(f1))) >= (double)576.0F) {
                                    if (biome$spawnlistentry == null) {
                                       List<MobSpawnSettings.SpawnerData> spawnerList = worldServerIn.getBiome(blockpos$mutableblockpos).value().getMobSettings().getMobs(enumcreaturetype).unwrap();
                                       if (spawnerList.isEmpty()) {
                                          break;
                                       }
                                       biome$spawnlistentry = net.minecraft.util.random.WeightedRandom.getRandomItem(worldServerIn.random, spawnerList).orElse(null);
                                       if (biome$spawnlistentry == null) {
                                          break;
                                       }
                                    }

                                    if (NaturalSpawner.isValidEmptySpawnBlock(worldServerIn, blockpos$mutableblockpos, worldServerIn.getBlockState(blockpos$mutableblockpos), worldServerIn.getFluidState(blockpos$mutableblockpos), biome$spawnlistentry.type)) {
                                       Mob entityliving;
                                       try {
                                          Entity entity = biome$spawnlistentry.type.create(worldServerIn);
                                          if (!(entity instanceof Mob)) continue;
                                          entityliving = (Mob)entity;
                                       } catch (Exception exception) {
                                          exception.printStackTrace();
                                          return j4;
                                       }

                                       if (entityliving != null) {
                                          entityliving.moveTo((double)f, (double)i3, (double)f1, worldServerIn.random.nextFloat() * 360.0F, 0.0F);
                                          boolean canSpawn = EventHooks.checkSpawnPosition(entityliving, worldServerIn, MobSpawnType.NATURAL);
                                          if (canSpawn) {
                                             ientitylivingdata = EventHooks.finalizeMobSpawn(entityliving, worldServerIn, worldServerIn.getCurrentDifficultyAt(entityliving.blockPosition()), MobSpawnType.NATURAL, ientitylivingdata);

                                             if (entityliving.checkSpawnObstruction(worldServerIn)) {
                                                ++j2;
                                                worldServerIn.addFreshEntityWithPassengers(entityliving);
                                             } else {
                                                entityliving.discard();
                                             }

                                             if (j2 >= entityliving.getMaxSpawnClusterSize()) {
                                                continue label153;
                                             }
                                          }

                                          j4 += j2;
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            return j4;
         }
      }
   }

   private static BlockPos getRandomChunkPosition(Level worldIn, int x, int z) {
      LevelChunk chunk = worldIn.getChunk(x, z);
      int i = x * 16 + worldIn.random.nextInt(16);
      int j = z * 16 + worldIn.random.nextInt(16);
      int k = Mth.roundToward(chunk.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, i & 15, j & 15) + 1, 16);
      int l = worldIn.random.nextInt(k > 0 ? k : chunk.getMaxBuildHeight() + 16 - 1);
      return new BlockPos(i, l, j);
   }

   public static boolean isValidEmptySpawnBlock(BlockState state) {
      if (state.isCollisionShapeFullBlock(null, BlockPos.ZERO)) {
         return false;
      } else if (state.isSignalSource()) {
         return false;
      } else if (!state.getFluidState().isEmpty()) {
         return false;
      } else {
         return !state.is(net.minecraft.tags.BlockTags.RAILS);
      }
   }

   public static boolean canCreatureTypeSpawnAtLocation(SpawnPlacementType spawnPlacementTypeIn, Level worldIn, BlockPos pos) {
      if (!worldIn.getWorldBorder().isWithinBounds(pos)) {
         return false;
      }
      BlockState blockState = worldIn.getBlockState(pos);
      if (spawnPlacementTypeIn == SpawnPlacementTypes.IN_WATER) {
         return worldIn.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER) && !blockState.isCollisionShapeFullBlock(worldIn, pos);
      } else {
         BlockPos below = pos.below();
         return worldIn.getBlockState(below).isValidSpawn(worldIn, below, null) && worldIn.isEmptyBlock(pos);
      }
   }

   public static boolean canCreatureTypeSpawnBody(SpawnPlacementType spawnPlacementTypeIn, Level worldIn, BlockPos pos, String spawnTypeString) {
      return MMSpawnChecks.canCreatureTypeSpawnHere(worldIn, pos, spawnPlacementTypeIn, spawnTypeString);
   }

   public static void performWorldGenSpawning(Level worldIn, net.minecraft.world.level.biome.Biome biomeIn, int centerX, int centerZ, int diameterX, int diameterZ, Random randomIn) {
      List<MobSpawnSettings.SpawnerData> list = biomeIn.getMobSettings().getMobs(MobCategory.CREATURE).unwrap();
      if (!list.isEmpty()) {
         while(randomIn.nextFloat() < biomeIn.getMobSettings().getCreatureProbability()) {
            MobSpawnSettings.SpawnerData biome$spawnlistentry = net.minecraft.util.random.WeightedRandom.getRandomItem(new net.minecraft.util.RandomSource() {
               public net.minecraft.util.RandomSource fork() { return this; }
               public net.minecraft.world.level.levelgen.PositionalRandomFactory forkPositional() { return null; }
               public void setSeed(long p_188583_) {}
               public int nextInt() { return randomIn.nextInt(); }
               public int nextInt(int p_188584_) { return randomIn.nextInt(p_188584_); }
               public long nextLong() { return randomIn.nextLong(); }
               public boolean nextBoolean() { return randomIn.nextBoolean(); }
               public float nextFloat() { return randomIn.nextFloat(); }
               public double nextDouble() { return randomIn.nextDouble(); }
               public double nextGaussian() { return randomIn.nextGaussian(); }
            }, list).orElse(null);
            if (biome$spawnlistentry == null) break;
            int i = biome$spawnlistentry.minCount + randomIn.nextInt(1 + biome$spawnlistentry.maxCount - biome$spawnlistentry.minCount);
            SpawnGroupData ientitylivingdata = null;
            int j = centerX + randomIn.nextInt(diameterX);
            int k = centerZ + randomIn.nextInt(diameterZ);
            int l = j;
            int i1 = k;

            for(int j1 = 0; j1 < i; ++j1) {
               boolean flag = false;

               for(int k1 = 0; !flag && k1 < 4; ++k1) {
                  BlockPos blockpos = worldIn.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(j, 0, k));
                  if (NaturalSpawner.isValidEmptySpawnBlock(worldIn, blockpos, worldIn.getBlockState(blockpos), worldIn.getFluidState(blockpos), biome$spawnlistentry.type)) {
                     Mob entityliving;
                     try {
                        Entity entity = biome$spawnlistentry.type.create(worldIn);
                        if (!(entity instanceof Mob)) continue;
                        entityliving = (Mob)entity;
                     } catch (Exception exception) {
                        exception.printStackTrace();
                        continue;
                     }

                     boolean canSpawn = EventHooks.checkSpawnPosition(entityliving, (ServerLevel)worldIn, MobSpawnType.NATURAL);
                     if (!canSpawn) {
                        continue;
                     }

                     entityliving.moveTo((double)((float)j + 0.5F), (double)blockpos.getY(), (double)((float)k + 0.5F), randomIn.nextFloat() * 360.0F, 0.0F);
                     worldIn.addFreshEntity(entityliving);
                     ientitylivingdata = entityliving.finalizeSpawn((ServerLevel)worldIn, worldIn.getCurrentDifficultyAt(entityliving.blockPosition()), MobSpawnType.NATURAL, ientitylivingdata);
                     flag = true;
                  }

                  j += randomIn.nextInt(5) - randomIn.nextInt(5);

                  for(k += randomIn.nextInt(5) - randomIn.nextInt(5); j < centerX || j >= centerX + diameterX || k < centerZ || k >= centerZ + diameterX; k = i1 + randomIn.nextInt(5) - randomIn.nextInt(5)) {
                     j = l + randomIn.nextInt(5) - randomIn.nextInt(5);
                  }
               }
            }
         }
      }

   }
}
