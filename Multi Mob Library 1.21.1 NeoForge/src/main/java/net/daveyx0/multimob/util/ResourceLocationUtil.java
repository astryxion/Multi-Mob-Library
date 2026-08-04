package net.daveyx0.multimob.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import net.daveyx0.multimob.core.MultiMob;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class ResourceLocationUtil {
   private static ResourceManager resourceManager;

   public static Resource getResource(String domain, String filename) {
      if (resourceManager == null) {
         resourceManager = Minecraft.getInstance().getResourceManager();
      }

      try {
         Optional<Resource> optionalResource = resourceManager.getResource(ResourceLocation.fromNamespaceAndPath(domain, filename));
         return optionalResource.orElse(null);
      } catch (Exception var3) {
         return null;
      }
   }

   public static void getModFileDirectories() {
      Resource resource = getResource("primitivemobs", "sounds.json");
      if (resource == null) return;
      try {
         InputStream stream = resource.open();
         BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

         try {
            while(reader.ready()) {
               MultiMob.LOGGER.info(reader.readLine());
            }
         } catch (IOException e) {
            e.printStackTrace();
         } finally {
            try {
               stream.close();
            } catch (IOException var11) {
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
