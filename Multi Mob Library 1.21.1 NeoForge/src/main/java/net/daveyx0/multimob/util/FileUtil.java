package net.daveyx0.multimob.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.io.FileUtils;

public class FileUtil {
   /**
    * Optional reference dumps for pack authors editing spawn configs.
    * Disabled by default — allBlockStates alone can be tens of MB with modded packs.
    */
   public static void createTextFilesForModInfo(File directory, boolean includeAllBlockStates) throws IOException {
      listResourcesForRegistry(directory, "allEntities", BuiltInRegistries.ENTITY_TYPE.keySet());
      listResourcesForRegistry(directory, "allItems", BuiltInRegistries.ITEM.keySet());
      listResourcesForRegistry(directory, "allBlocks", BuiltInRegistries.BLOCK.keySet());
      listResourcesForRegistry(directory, "allPotions", BuiltInRegistries.MOB_EFFECT.keySet());
      listResourcesForRegistry(directory, "allPotionTypes", BuiltInRegistries.POTION.keySet());
      // Enchantments are datapack registries in 1.21; skip static dump.
      listBiomeResources(directory, "allBiomes", "allBiomeTypes");
      if (includeAllBlockStates) {
         listBlockStateResources(directory, "allBlockStates");
      }
      listStructureResources(directory, "allVanillaStructures");
   }

   /** Removes the known oversized reference dump from older Multi Mob versions. */
   public static boolean cleanupOversizedReferenceFiles(File directory) {
      if (directory == null || !directory.exists()) {
         return false;
      }
      File blockStates = new File(directory, "allBlockStates.txt");
      return blockStates.exists() && blockStates.isFile() && blockStates.delete();
   }

   public static void listResourcesForRegistry(File directory, String fileName, Set<ResourceLocation> registry) throws IOException {
      List<String> arrayList = new ArrayList<>();

      for (ResourceLocation loc : registry) {
         arrayList.add(loc.toString());
      }

      createTextFile(directory, fileName, arrayList);
   }

   public static void listBlockStateResources(File directory, String fileName) throws IOException {
      List<String> arrayList = new ArrayList<>();

      for (Block loc : BuiltInRegistries.BLOCK) {
         for (BlockState state : loc.getStateDefinition().getPossibleStates()) {
            arrayList.add(state.toString());
         }
      }

      createTextFile(directory, fileName, arrayList);
   }

   public static void listBiomeResources(File directory, String fileName, String fileName2) throws IOException {
      // Biomes are datapack registries; dump common vanilla biome ids as a reference aid.
      List<String> arrayList = new ArrayList<>();
      arrayList.add("minecraft:plains");
      arrayList.add("minecraft:forest");
      arrayList.add("minecraft:desert");
      arrayList.add("minecraft:ocean");
      arrayList.add("minecraft:nether_wastes");
      arrayList.add("minecraft:the_end");
      List<String> arrayListTypes = new ArrayList<>();
      arrayListTypes.add("minecraft:is_overworld");
      arrayListTypes.add("minecraft:is_forest");
      arrayListTypes.add("minecraft:is_ocean");
      arrayListTypes.add("minecraft:is_nether");
      createTextFile(directory, fileName, arrayList);
      createTextFile(directory, fileName2, arrayListTypes);
   }

   public static void listStructureResources(File directory, String fileName) throws IOException {
      List<String> arrayList = new ArrayList<>();
      arrayList.add("Stronghold");
      arrayList.add("Mansion");
      arrayList.add("Monument");
      arrayList.add("Village");
      arrayList.add("Mineshaft");
      arrayList.add("Temple");
      arrayList.add("Fortress");
      arrayList.add("EndCity");
      createTextFile(directory, fileName, arrayList);
   }

   public static void createTextFile(File directory, String fileName, List<String> list) throws IOException {
      File file = new File(directory, fileName + ".txt");
      FileUtils.writeLines(file, list);
   }
}
