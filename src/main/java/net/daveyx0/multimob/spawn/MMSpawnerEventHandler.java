package net.daveyx0.multimob.spawn;

import java.util.ArrayList;
import java.util.List;
import net.daveyx0.multimob.common.capabilities.CapabilityVariantEntity;
import net.daveyx0.multimob.common.capabilities.IVariantEntity;
import net.daveyx0.multimob.config.MMConfigSpawns;
import net.daveyx0.multimob.entity.EntityDummy;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.LevelData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public class MMSpawnerEventHandler {
   private MMWorldSpawner worldSpawner = null;

   @SubscribeEvent
   public void onWorldTickEvent(LevelTickEvent.Pre event) {
      if (event.getLevel() instanceof ServerLevel worldServer) {
         if (MMConfigSpawns.getUseAdditionalSpawning()) {
            if (this.worldSpawner == null) {
               this.worldSpawner = new MMWorldSpawner();
            }

            LevelData worldInfo = worldServer.getLevelData();
            if (worldInfo.getGameTime() % (long)MMConfigSpawns.getSpawnTickDelay() == 0L) {
               this.worldSpawner.findChunksForSpawning(worldServer, worldServer.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOMOBSPAWNING), true, true);
            }
         } else {
            this.worldSpawner = null;
         }
      }
   }

   @SubscribeEvent
   public void onCheckSpawn(FinalizeSpawnEvent event) {
      if (event.getSpawner() == null && !event.isSpawnCancelled()) {
         if (event.getEntity() instanceof EntityDummy) {
            event.setSpawnCancelled(true);
            return;
         }

         List<MMSpawnEntry> spawnEntries = new ArrayList<>();

         for (MMSpawnEntry entry : MMSpawnRegistry.getSpawnEntries()) {
            if (entry.getEntityType().equals(event.getEntity().getType())) {
               spawnEntries.add(entry);
            }
         }

         if (!spawnEntries.isEmpty()) {
            BlockPos spawnPos = BlockPos.containing(event.getX(), event.getY(), event.getZ());
            MMSpawnEntry entry = null;
            if (event.getLevel() instanceof ServerLevel serverLevel && event.getEntity() instanceof Mob) {
               entry = selectSpawnEntry(serverLevel, spawnEntries, event.getEntity(), spawnPos);
            }

            if (entry == null) {
               event.setSpawnCancelled(true);
            } else {
               final MMSpawnEntry selectedEntry = entry;
               if (selectedEntry.getVariantID() != 0 && CapabilityVariantEntity.EventHandler.hasVariant(event.getEntity())) {
                  IVariantEntity variant = event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY);
                  if (variant != null) {
                     variant.setVariant(selectedEntry.getVariantID());
                  }
               }

               if (selectedEntry.getOverrideCanGetSpawnHere()) {
                  if (!MMSpawnChecks.canEntitySpawnHere(event.getEntity(), selectedEntry)) {
                     event.setSpawnCancelled(true);
                  }
               }
            }
         }
      }
   }

   private static MMSpawnEntry selectSpawnEntry(ServerLevel level, List<MMSpawnEntry> spawnEntries, Mob mob, BlockPos pos) {
      List<MMSpawnEntry> passing = new ArrayList<>();

      for (MMSpawnEntry candidate : spawnEntries) {
         if (!MMSpawnChecks.performSpawnChecks(level, pos, candidate)) {
            continue;
         }

         if (!candidate.getOverrideCanGetSpawnHere() || MMSpawnChecks.canEntitySpawnHere(mob, candidate)) {
            passing.add(candidate);
         }
      }

      if (passing.isEmpty()) {
         return null;
      }

      if (passing.size() == 1) {
         return passing.get(0);
      }

      int totalWeight = 0;
      for (MMSpawnEntry candidate : passing) {
         totalWeight += Math.max(1, candidate.getSpawnWeight());
      }

      int roll = level.getRandom().nextInt(totalWeight);
      for (MMSpawnEntry candidate : passing) {
         roll -= Math.max(1, candidate.getSpawnWeight());
         if (roll < 0) {
            return candidate;
         }
      }

      return passing.get(passing.size() - 1);
   }

   public MMWorldSpawner getWorldSpawner() {
      return this.worldSpawner;
   }
}
