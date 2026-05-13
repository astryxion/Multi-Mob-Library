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
            MMSpawnEntry entry = (MMSpawnEntry)spawnEntries.get(event.getLevel().getRandom().nextInt(spawnEntries.size()));
            if (entry != null) {
               if (entry.getVariantID() != 0 && CapabilityVariantEntity.EventHandler.hasVariant(event.getEntity())) {
                  event.getEntity().getCapability(CapabilityVariantEntity.VARIANT_ENTITY_CAPABILITY).ifPresent(variant -> {
                     variant.setVariant(entry.getVariantID());
                  });
               }

               if (event.getLevel() instanceof ServerLevel && MMSpawnChecks.performSpawnChecks((ServerLevel)event.getLevel(), new BlockPos((int)event.getX(), (int)event.getY(), (int)event.getZ()), entry)) {
                  if (!entry.getOverrideCanGetSpawnHere()) {
                     event.setResult(Event.Result.DEFAULT);
                     return;
                  }

                  if (MMSpawnChecks.canEntitySpawnHere(event.getEntity(), entry)) {
                     event.setResult(Event.Result.ALLOW);
                  } else {
                     event.setResult(Event.Result.DENY);
                  }
               } else {
                  event.setResult(Event.Result.DENY);
               }
            }

         }
      }
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
