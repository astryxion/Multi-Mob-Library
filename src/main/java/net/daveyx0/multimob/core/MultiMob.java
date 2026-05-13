package net.daveyx0.multimob.core;

import java.io.File;
import java.io.IOException;
import net.daveyx0.multimob.config.MMConfig;
import net.daveyx0.multimob.config.MMFactoryGui;
import net.daveyx0.multimob.message.MMMessageRegistry;
import net.daveyx0.multimob.spawn.MMSpawnRegistry;
import net.daveyx0.multimob.spawn.MMSpawnerEventHandler;
import net.daveyx0.multimob.util.FileUtil;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("multimob")
public class MultiMob {
   public static final Logger LOGGER = LogManager.getLogger("multimob");
   public static MultiMob instance;
   private File directory;

   public static class MobCategoryEnumParams {
      public static final EnumProxy<MobCategory> MULTIMOB_MONSTER_PROXY = new EnumProxy<>(MobCategory.class, "multimob:multimob_monster", 35, false, false, 128);
      public static final EnumProxy<MobCategory> MULTIMOB_PASSIVE_PROXY = new EnumProxy<>(MobCategory.class, "multimob:multimob_passive", 10, false, false, 128);
      public static final EnumProxy<MobCategory> MULTIMOB_WATER_PROXY = new EnumProxy<>(MobCategory.class, "multimob:multimob_water", 10, false, false, 128);
      public static final EnumProxy<MobCategory> MULTIMOB_LAVA_PROXY = new EnumProxy<>(MobCategory.class, "multimob:multimob_lava", 5, false, false, 128);
   }

   public static final MobCategory MULTIMOB_MONSTER = MobCategoryEnumParams.MULTIMOB_MONSTER_PROXY.getValue();
   public static final MobCategory MULTIMOB_PASSIVE = MobCategoryEnumParams.MULTIMOB_PASSIVE_PROXY.getValue();
   public static final MobCategory MULTIMOB_WATER = MobCategoryEnumParams.MULTIMOB_WATER_PROXY.getValue();
   public static final MobCategory MULTIMOB_LAVA = MobCategoryEnumParams.MULTIMOB_LAVA_PROXY.getValue();

   public MultiMob() {
      instance = this;
      ModLoadingContext context = ModLoadingContext.get();
      IEventBus modEventBus = context.getActiveContainer().getEventBus();

      MMCapabilities.registerAttachments(modEventBus);
      MMEntityRegistry.init(modEventBus);

      modEventBus.addListener(this::commonSetup);
      modEventBus.addListener(this::clientSetup);
      modEventBus.addListener(MMCapabilities::registerCapabilities);
      modEventBus.addListener(MMConfig::onConfigChanged);
      modEventBus.addListener(MMMessageRegistry::registerPayloads);

      context.getActiveContainer().registerConfig(ModConfig.Type.COMMON, MMConfig.CONFIG_SPEC, "multimob/multimob_spawns.toml");
      if (FMLEnvironment.dist == Dist.CLIENT) {
         context.getActiveContainer().registerExtensionPoint(net.neoforged.neoforge.client.gui.IConfigScreenFactory.class, MMFactoryGui.getFactory());
      }

      NeoForge.EVENT_BUS.register(new MMSpawnerEventHandler());
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
         if (!subDirectory.exists()) {
            subDirectory.mkdirs();
         }

         try {
            FileUtil.createTextFilesForModInfo(subDirectory);
         } catch (IOException e) {
            e.printStackTrace();
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
