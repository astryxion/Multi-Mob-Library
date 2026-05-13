package net.daveyx0.multimob.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.commons.io.FileUtils;

public class FileUtil {
   public static void createTextFilesForModInfo(File directory) throws IOException {
      listResourcesForRegistry(directory, "allEntities", BuiltInRegistries.ENTITY_TYPE.keySet());
      listResourcesForRegistry(directory, "allItems", BuiltInRegistries.ITEM.keySet());
      listResourcesForRegistry(directory, "allBlocks", BuiltInRegistries.BLOCK.keySet());
      listResourcesForRegistry(directory, "allPotions", BuiltInRegistries.MOB_EFFECT.keySet());
      listResourcesForRegistry(directory, "allPotionTypes", BuiltInRegistries.POTION.keySet());
      listResourcesForRegistry(directory, "allEnchantments", BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE.keySet());
      listBiomeResources(directory, "allBiomes", "allBiomeTypes");
      listBlockStateResources(directory, "allBlockStates");
      listStructureResources(directory, "allVanillaStructures");
   }

   public static void listResourcesForRegistry(File directory, String fileName, Set<ResourceLocation> registry) throws IOException {
      List<String> arrayList = new ArrayList();

      for(ResourceLocation loc : registry) {
         arrayList.add(loc.toString());
      }

      createTextFile(directory, fileName, arrayList);
   }

   public static void listBlockStateResources(File directory, String fileName) throws IOException {
      new ArrayList();
      List<String> arrayList = new ArrayList();

      for(Block loc : BuiltInRegistries.BLOCK) {
         for(BlockState state : loc.getStateDefinition().getPossibleStates()) {
            arrayList.add(state.toString());
         }
      }

      createTextFile(directory, fileName, arrayList);
   }

   public static HolderLookup.RegistryLookup<Biome> getBiomeLookup() {
      HolderLookup.RegistryLookup<Biome> biomeLookup = VanillaRegistries.createLookup().lookupOrThrow(Registries.BIOME);
      if (FMLEnvironment.dist == Dist.CLIENT) {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft != null) {
            RegistryAccess.Frozen base = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
            RegistryAccess.Frozen loaded = RegistryDataLoader.load(
               minecraft.getResourceManager(),
               base,
               RegistryDataLoader.WORLDGEN_REGISTRIES.stream().filter(data -> data.key().equals(Registries.BIOME)).toList()
            );
            HolderLookup.RegistryLookup<Biome> loadedLookup = loaded.lookupOrThrow(Registries.BIOME);
            if (loadedLookup.listElements().findAny().isPresent()) {
               biomeLookup = loadedLookup;
            }
         }
      }

      return biomeLookup;
   }

   public static void listBiomeResources(File directory, String fileName, String fileName2) throws IOException {
      List<String> arrayList = new ArrayList();
      List<String> arrayListTypes = new ArrayList();
      HolderLookup.RegistryLookup<Biome> biomeRegistry = getBiomeLookup();

      for(Holder.Reference<Biome> biomeHolder : biomeRegistry.listElements().toList()) {
         arrayList.add(biomeHolder.key().location().toString());
         biomeHolder.tags().forEach(tag -> {
            String tagName = tag.location().toString();
            if (!arrayListTypes.contains(tagName)) {
               arrayListTypes.add(tagName);
            }
         });
      }

      createTextFile(directory, fileName, arrayList);
      createTextFile(directory, fileName2, arrayListTypes);
   }

   public static void listStructureResources(File directory, String fileName) throws IOException {
      List<String> arrayList = new ArrayList();
      arrayList.add("Stronghold");
      arrayList.add("Mansion");
      arrayList.add("Monument");
      arrayList.add("Village");
      arrayList.add("Mineshaft");
      arrayList.add("Temple");
      arrayList.add("Fortress");
      arrayList.add("MineshaftMesa");
      arrayList.add("Mansion");
      arrayList.add("OceanMonument");
      arrayList.add("EndCity");
      arrayList.add("Igloo");
      arrayList.add("DesertPyramid");
      arrayList.add("JunglePyramid");
      arrayList.add("SwampHut");
      arrayList.add("OceanRuin");
      arrayList.add("Shipwreck");
      arrayList.add("BuriedTreasure");
      arrayList.add("PillagerOutpost");
      arrayList.add("BastionRemnant");
      arrayList.add("RuinedPortal");
      createTextFile(directory, fileName, arrayList);
   }

   public static void createTextFile(File directory, String fileName, List<String> arrayList) throws IOException {
      File file = new File(directory, fileName + ".txt");
      FileUtils.writeLines(file, arrayList);
   }
}
