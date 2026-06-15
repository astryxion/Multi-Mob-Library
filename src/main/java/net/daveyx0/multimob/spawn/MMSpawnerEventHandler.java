package net.daveyx0.multimob.spawn;

import java.util.ArrayList;
import java.util.List;
import net.daveyx0.multimob.common.capabilities.CapabilityVariantEntity;
import net.daveyx0.multimob.common.capabilities.IVariantEntity;
import net.daveyx0.multimob.config.MMConfigSpawns;
import net.daveyx0.multimob.entity.EntityDummy;
import net.daveyx0.multimob.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.LevelData;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

public class MMSpawnerEventHandler {
   private MMWorldSpawner worldSpawner = null;

   @SubscribeEvent
   public void onWorldTickEvent(TickEvent.LevelTickEvent event) {
      if (event.phase == TickEvent.Phase.START && event.level instanceof ServerLevel) {
         ServerLevel worldServer = (ServerLevel)event.level;
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
   public void onCheckSpawn(MobSpawnEvent.FinalizeSpawn event) {
      if (event.getSpawner() == null && event.getResult() != Event.Result.DENY) {
         if (event.getEntity() instanceof EntityDummy) {
            event.setResult(Event.Result.DENY);
         }

         List<MMSpawnEntry> spawnEntries = new ArrayList();

         for(MMSpawnEntry entry : MMSpawnRegistry.getSpawnEntries()) {
            if (entry.getEntityType().equals(event.getEntity().getType())) {
               spawnEntries.add(entry);
            }
         }

         if (spawnEntries != null && !spawnEntries.isEmpty()) {
            BlockPos spawnPos = BlockPos.containing(event.getX(), event.getY(), event.getZ());
            MMSpawnEntry entry = null;
            if (event.getLevel() instanceof ServerLevel serverLevel && event.getEntity() instanceof Mob) {
               entry = selectSpawnEntry(serverLevel, spawnEntries, (Mob)event.getEntity(), spawnPos);
            }

            if (entry == null) {
               event.setResult(Event.Result.DENY);
            } else {
               final MMSpawnEntry selectedEntry = entry;
               if (selectedEntry.getVariantID() != 0 && CapabilityVariantEntity.EventHandler.hasVariant(event.getEntity())) {
                  event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY).ifPresent(variant -> {
                     variant.setVariant(selectedEntry.getVariantID());
                  });
               }

               if (!selectedEntry.getOverrideCanGetSpawnHere()) {
                  event.setResult(Event.Result.DEFAULT);
               } else if (MMSpawnChecks.canEntitySpawnHere(event.getEntity(), selectedEntry)) {
                  event.setResult(Event.Result.ALLOW);
               } else {
                  event.setResult(Event.Result.DENY);
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

   @SubscribeEvent
   public void onLivingPackSizeEvent(LivingPackSizeEvent event) {
      MMSpawnEntry entry = MMSpawnRegistry.getSpawnEntryFromEntityType(event.getEntity().getType());
      if (entry != null && entry.getGroupSizeRange() != null && entry.getGroupSizeRange()[1] > 0) {
         event.setMaxPackSize(entry.getGroupSizeRange()[1]);
         event.setResult(Event.Result.ALLOW);
      }

   }

   public MMWorldSpawner getWorldSpawner() {
      return this.worldSpawner;
   }
}
