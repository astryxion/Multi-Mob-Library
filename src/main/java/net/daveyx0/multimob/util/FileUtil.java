package net.daveyx0.multimob.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.FileUtils;

public class FileUtil {
   public static void createTextFilesForModInfo(File directory) throws IOException {
      listResourcesForRegistry(directory, "allEntities", ForgeRegistries.ENTITY_TYPES.getKeys());
      listResourcesForRegistry(directory, "allItems", ForgeRegistries.ITEMS.getKeys());
      listResourcesForRegistry(directory, "allBlocks", ForgeRegistries.BLOCKS.getKeys());
      listResourcesForRegistry(directory, "allPotions", ForgeRegistries.MOB_EFFECTS.getKeys());
      listResourcesForRegistry(directory, "allPotionTypes", ForgeRegistries.POTIONS.getKeys());
      listResourcesForRegistry(directory, "allEnchantments", ForgeRegistries.ENCHANTMENTS.getKeys());
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

      for(Block loc : ForgeRegistries.BLOCKS) {
         for(BlockState state : loc.getStateDefinition().getPossibleStates()) {
            arrayList.add(state.toString());
         }
      }

      createTextFile(directory, fileName, arrayList);
   }

   public static void listBiomeResources(File directory, String fileName, String fileName2) throws IOException {
      List<String> arrayList = new ArrayList();
      List<String> arrayListTypes = new ArrayList();

      for(ResourceLocation loc : ForgeRegistries.BIOMES.getKeys()) {
         arrayList.add(loc.toString());
         Biome biome = ForgeRegistries.BIOMES.getValue(loc);
         if (biome != null && ForgeRegistries.BIOMES.getHolder(ForgeRegistries.BIOMES.getResourceKey(biome).get()).isPresent()) {
            net.minecraft.core.Holder<Biome> biomeHolder = ForgeRegistries.BIOMES.getHolder(ForgeRegistries.BIOMES.getResourceKey(biome).get()).get();
            biomeHolder.tags().forEach(tag -> {
               String tagName = tag.location().toString();
               if (!arrayListTypes.contains(tagName)) {
                  arrayListTypes.add(tagName);
               }
            });
         }
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
      arrayList.add("EndCity");
      createTextFile(directory, fileName, arrayList);
   }

   public static void createTextFile(File directory, String fileName, List<String> list) throws IOException {
      File textFile = new File(directory, fileName + ".txt");
      if (textFile.exists()) {
         textFile.delete();
      }

      FileUtils.writeLines(textFile, list);
   }
}
