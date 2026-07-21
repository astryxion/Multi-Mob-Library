package net.daveyx0.multimob.core;

import java.io.File;
import java.io.IOException;
import net.daveyx0.multimob.config.MMConfig;
import net.daveyx0.multimob.config.MMConfigSpawns;
import net.daveyx0.multimob.config.MMFactoryGui;
import net.daveyx0.multimob.entity.IMultiMob;
import net.daveyx0.multimob.entity.IMultiMobLava;
import net.daveyx0.multimob.entity.IMultiMobPassive;
import net.daveyx0.multimob.entity.IMultiMobWater;
import net.daveyx0.multimob.message.MMMessageRegistry;
import net.daveyx0.multimob.spawn.MMSpawnRegistry;
import net.daveyx0.multimob.spawn.MMSpawnerEventHandler;
import net.daveyx0.multimob.util.FileUtil;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("multimob")
public class MultiMob {
   public static final Logger LOGGER = LogManager.getLogger("multimob");
   public static MultiMob instance;
   private File directory;

   // Kept for API compatibility with older Multi Mob dependents, but Primitive Mobs
   // now uses vanilla MobCategory values so NaturalSpawner does not run extra category passes.
   public static final MobCategory MULTIMOB_MONSTER = MobCategory.create("MULTIMOB_MONSTER", "multimob_monster", 0, false, false, 128);
   public static final MobCategory MULTIMOB_PASSIVE = MobCategory.create("MULTIMOB_PASSIVE", "multimob_passive", 0, true, true, 128);
   public static final MobCategory MULTIMOB_WATER = MobCategory.create("MULTIMOB_WATER", "multimob_water", 0, true, false, 128);
   public static final MobCategory MULTIMOB_LAVA = MobCategory.create("MULTIMOB_LAVA", "multimob_lava", 0, false, false, 128);

   public MultiMob() {
      instance = this;
      IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

      MMEntityRegistry.init(modEventBus);

      modEventBus.addListener(this::commonSetup);
      modEventBus.addListener(this::clientSetup);
      modEventBus.addListener(MMCapabilities::registerCapabilities);
      modEventBus.addListener(MMConfig::onConfigChanged);

      ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MMConfig.CONFIG_SPEC, "multimob/multimob_spawns.toml");

      MinecraftForge.EVENT_BUS.register(this);
      MinecraftForge.EVENT_BUS.register(new MMEvents.EntityEventHandler());
      MinecraftForge.EVENT_BUS.register(new MMSpawnerEventHandler());
   }

   private void commonSetup(final FMLCommonSetupEvent event) {
      event.enqueueWork(() -> {
         MMMessageRegistry.preInit();

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
