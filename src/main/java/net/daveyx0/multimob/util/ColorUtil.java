package net.daveyx0.multimob.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import com.mojang.blaze3d.platform.NativeImage;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;

@OnlyIn(Dist.CLIENT)
public class ColorUtil {
   public static int[] getBlockStateColor(BlockState state, @Nullable BlockPos pos, @Nullable Level worldObj, boolean useExtendedState) {
      return getBlockStateColor(state, pos, worldObj, Direction.UP, useExtendedState);
   }

   public static int[] getBlockStateColor(BlockState state, @Nullable BlockPos pos, @Nullable Level worldObj, Direction face, boolean useExtendedState) {
      int[] color = new int[3];
      int colorMultiplier = -1;
      BlockState backupState = state;

      colorMultiplier = Minecraft.getInstance().getBlockColors().getColor(state, worldObj, pos, 0);

      TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getTexture(state, worldObj, pos);

      if (sprite != null) {
         String textureName = sprite.contents().name().toString();
         if (textureName.equals("missingno")) {
            state = backupState;
            colorMultiplier = Minecraft.getInstance().getBlockColors().getColor(backupState, worldObj, pos, 0);
            sprite = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getTexture(backupState, worldObj, pos);
         }
      }

      if (state.getBlock() != Blocks.AIR) {
         Color colour = new Color(colorMultiplier, true);
         if (colorMultiplier != -1 && (colour.getRed() != colour.getGreen() || colour.getGreen() != colour.getBlue() || colour.getBlue() != colour.getRed()) && state.getBlock() != Blocks.TALL_GRASS) {
            color[0] = colour.getRed();
            color[1] = colour.getGreen();
            color[2] = colour.getBlue();
         } else if (sprite != null) {
            String textureName = sprite.contents().name().toString();
            String modelName = textureName.replaceAll(":", ":models/");
            ModelResourceLocation model = new ModelResourceLocation(ResourceLocation.parse(modelName), "");
            String topTextureName = "";
            if (model != null) {
               BakedModel bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(state);
               if (bakedModel != null && !ModList.get().isLoaded("codechickenlib") && !ModList.get().isLoaded("botania")) {
                  List<BakedQuad> quads = bakedModel.getQuads(state, face, RandomSource.create(1L));
                  if (quads != null && !quads.isEmpty() && quads.size() > 0) {
                     topTextureName = quads.get(0).getSprite().contents().name().toString();
                  }
               }
            }

            if (!topTextureName.equals("") && !topTextureName.equals("missingno")) {
               color = getTextureColor(topTextureName, "blocks");
            } else if (!textureName.equals("") && !textureName.equals("missingno")) {
               color = getTextureColor(textureName, "blocks");
            }
         }

         if (color == null && colorMultiplier == -1) {
            MapColor mapcolor = state.getMapColor(worldObj, pos);
            if (mapcolor != null) {
               colorMultiplier = mapcolor.col;
            }
         }
      }

      if (isColorInvalid(color) && useExtendedState) {
         color = ColorUtil.getBlockStateColor(state, pos, worldObj, face, false);
      }

      return color;
   }

   public static int[] getItemStackColor(ItemStack stack, Level worldObj) {
      int[] color = new int[3];
      int colorMultiplier = -1;
      colorMultiplier = Minecraft.getInstance().getItemColors().getColor(stack, 0);
      if (colorMultiplier != -1) {
         Color colour = new Color(colorMultiplier, true);
         color[0] = colour.getRed();
         color[1] = colour.getGreen();
         color[2] = colour.getBlue();
      } else {
         String textureName = "";
         BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, worldObj, (LivingEntity)null, 0);
         List<BakedQuad> quads = model.getQuads((BlockState)null, (Direction)null, RandomSource.create(1L));
         if (quads != null && !quads.isEmpty() && quads.size() > 0) {
            textureName = quads.get(0).getSprite().contents().name().toString();
         }

         if (!textureName.equals("") && !textureName.equals("missingno")) {
            color = getTextureColor(textureName, "items");
         }
      }

      return color;
   }

   public static int[] getTextureColor(String name, String type) {
      int[] rgb = null;
      Resource resource = getResource(name, type);
      if (resource != null) {
         try {
            InputStream stream = resource.open();
            try {
               rgb = ImageUtil.main(stream);
            } catch (Exception e) {
               e.printStackTrace();
            } finally {
               try {
                  stream.close();
               } catch (IOException var13) {
               }
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      return rgb;
   }

   public static Resource getResource(String name, String type) {
      ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
      Resource resource = null;
      if (name.contains(":")) {
         String[] divided = name.split(":");
         if (divided != null && divided.length > 0) {
            resource = ResourceLocationUtil.getResource(divided[0].toString(), "textures/" + divided[1] + ".png");
         }
      } else {
         resource = ResourceLocationUtil.getResource("minecraft", "textures/" + type + "/" + name + ".png");
      }
      return resource;
   }

   public static int[] setBrightness(int[] rgb, float brightness) {
      float newRed = (float)rgb[0] / 255.0F;
      float newGreen = (float)rgb[1] / 255.0F;
      float newBlue = (float)rgb[2] / 255.0F;
      if (brightness < 0.0F) {
         float newBrightness = brightness * -1.0F;
         newBrightness = 1.0F - newBrightness / 100.0F;
         newRed *= newBrightness;
         newGreen *= newBrightness;
         newBlue *= newBrightness;
      } else if (brightness > 0.0F) {
         float newColor = brightness / 100.0F;
         float tempRed = (1.0F - newRed) * newColor;
         float tempGreen = (1.0F - newGreen) * newColor;
         float tempBlue = (1.0F - newBlue) * newColor;
         newRed += tempRed;
         newGreen += tempGreen;
         newBlue += tempBlue;
      }

      if (newRed > 1.0F) {
         newRed = 1.0F;
      }

      if (newGreen > 1.0F) {
         newGreen = 1.0F;
      }

      if (newBlue > 1.0F) {
         newBlue = 1.0F;
      }

      int[] newColor = new int[3];
      newColor[0] = Math.round(newRed * 255.0F);
      newColor[1] = Math.round(newGreen * 255.0F);
      newColor[2] = Math.round(newBlue * 255.0F);
      return newColor;
   }

   public static int getBlockColor(Entity entity) {
      int i = Mth.floor(entity.getX());
      int j = Mth.floor(entity.getBoundingBox().minY);
      int k = Mth.floor(entity.getZ());
      if (entity.level().getBlockState(new BlockPos(i, j, k)).getBlock() == Blocks.AIR) {
         j = Mth.floor(entity.getBoundingBox().minY - 0.1);
      }

      BlockPos pos = new BlockPos(i, j, k);
      BlockState state = entity.level().getBlockState(pos);
      Minecraft.getInstance().getBlockColors().getColor(state, entity.level(), pos, 0);
      if (state.getBlock() != Blocks.AIR) {
         int[] newColor = getBlockStateColor(state, pos, entity.level(), false);
         if (newColor != null) {
            Color color = new Color(newColor[0], newColor[1], newColor[2]);
            return color.hashCode();
         }
      }

      return -1;
   }

   public static BlockState getBlockState(Entity entity) {
      int i = Mth.floor(entity.getX());
      int j = Mth.floor(entity.getBoundingBox().minY);
      int k = Mth.floor(entity.getZ());
      if (entity.level().getBlockState(new BlockPos(i, j, k)).getBlock() == Blocks.AIR) {
         j = Mth.floor(entity.getBoundingBox().minY - 0.1);
      }

      BlockPos pos = new BlockPos(i, j, k);
      BlockState state = entity.level().getBlockState(pos);
      return state;
   }

   @Nullable
   public static ResourceLocation convertToGreyScale(ResourceLocation resource) {
      ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
      ResourceLocation greyscaleTexture = null;

      try {
         Optional<Resource> optionalResource = resourceManager.getResource(resource);
         if (optionalResource.isPresent()) {
            NativeImage nativeImage = NativeImage.read(optionalResource.get().open());
            int width = nativeImage.getWidth();
            int height = nativeImage.getHeight();
            NativeImage greyImage = new NativeImage(width, height, false);
            for (int x = 0; x < width; x++) {
               for (int y = 0; y < height; y++) {
                  int pixel = nativeImage.getPixelRGBA(x, y);
                  int a = (pixel >> 24) & 0xFF;
                  int b = (pixel >> 16) & 0xFF;
                  int g = (pixel >> 8) & 0xFF;
                  int r = pixel & 0xFF;
                  int grey = (r + g + b) / 3;
                  greyImage.setPixelRGBA(x, y, (a << 24) | (grey << 16) | (grey << 8) | grey);
               }
            }
            greyscaleTexture = ResourceLocation.fromNamespaceAndPath(resource.getNamespace(), resource.getPath());
            Minecraft.getInstance().getTextureManager().register(greyscaleTexture, new DynamicTexture(greyImage));
            nativeImage.close();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

      return greyscaleTexture;
   }

   public static boolean isColorInvalid(int[] color) {
      return color == null || color.length == 0 || color[0] == 0 && color[1] == 0 && color[2] == 0;
   }
}
