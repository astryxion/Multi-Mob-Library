package net.daveyx0.multimob.core;

import java.io.File;
import java.io.IOException;
import net.daveyx0.multimob.config.MMConfig;
import net.daveyx0.multimob.config.MMConfigSpawns;
import net.daveyx0.multimob.message.MMMessageRegistry;
import net.daveyx0.multimob.spawn.MMSpawnRegistry;
import net.daveyx0.multimob.spawn.MMSpawnerEventHandler;
import net.daveyx0.multimob.util.FileUtil;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("multimob")
public class MultiMob {
   public static final Logger LOGGER = LogManager.getLogger("multimob");
   public static MultiMob instance;
   private File directory;

   // Custom MobCategory.create() is an enum-extension concern on NeoForge 1.21.
   // Alias to vanilla categories for API compatibility; custom spawning uses MMWorldSpawner.
   public static final MobCategory MULTIMOB_MONSTER = MobCategory.MONSTER;
   public static final MobCategory MULTIMOB_PASSIVE = MobCategory.CREATURE;
   public static final MobCategory MULTIMOB_WATER = MobCategory.WATER_CREATURE;
   public static final MobCategory MULTIMOB_LAVA = MobCategory.MONSTER;

   public MultiMob(IEventBus modEventBus, ModContainer modContainer) {
      instance = this;

      MMEntityRegistry.init(modEventBus);
      MMAttachments.init(modEventBus);

      modEventBus.addListener(this::commonSetup);
      modEventBus.addListener(this::clientSetup);
      modEventBus.addListener(MMCapabilities::registerCapabilities);
      modEventBus.addListener(MMConfig::onConfigChanged);
      modEventBus.addListener(MMMessageRegistry::registerPayloads);

      modContainer.registerConfig(ModConfig.Type.COMMON, MMConfig.CONFIG_SPEC, "multimob/multimob_spawns.toml");

      NeoForge.EVENT_BUS.register(new MMSpawnerEventHandler());
   }

   private void commonSetup(final FMLCommonSetupEvent event) {
      event.enqueueWork(() -> {
         this.directory = new File(FMLPaths.CONFIGDIR.get().toFile(), "multimob");
         if (!this.directory.exists()) {
            this.directory.mkdirs();
         }

         MMEntityRegistry.addDummy();

         MMTameableEntries.registerTameables();
         MMVariantEntries.registerVariants();
         MMSpawnRegistry.registerFillerSpawns();

         File subDirectory = new File(this.directory, "modInformation");
         // Always remove the legacy allBlockStates dump — it is not used at runtime and
         // balloons to tens of MB in modpacks, slowing exports and wasting disk.
         if (FileUtil.cleanupOversizedReferenceFiles(subDirectory)) {
            LOGGER.info("Removed oversized config/multimob/modInformation/allBlockStates.txt reference dump");
         }

         if (MMConfigSpawns.GENERATE_MOD_INFORMATION.get()) {
            if (!subDirectory.exists()) {
               subDirectory.mkdirs();
            }

            try {
               FileUtil.createTextFilesForModInfo(subDirectory, MMConfigSpawns.GENERATE_ALL_BLOCKSTATES.get());
            } catch (IOException e) {
               e.printStackTrace();
            }
         }

         MMConfig.postInit();
      });
   }

   private void clientSetup(final FMLClientSetupEvent event) {
      event.enqueueWork(() -> {
         net.daveyx0.multimob.client.renderer.MMRenderManager.init();
      });
   }

   public File getDirectory() {
      return this.directory;
   }
}
