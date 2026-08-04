package net.daveyx0.multimob.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.daveyx0.multimob.core.MultiMob;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ImageUtil {
   public static int[] main(InputStream fileloc) throws Exception {
      ImageInputStream is = ImageIO.createImageInputStream(fileloc);
      Iterator iter = ImageIO.getImageReaders(is);
      if (!iter.hasNext()) {
         MultiMob.LOGGER.error("Cannot load the specified file ");
      }

      ImageReader imageReader = (ImageReader)iter.next();
      imageReader.setInput(is);
      BufferedImage imageOld = imageReader.read(0);
      BufferedImage image = getScaledImage(imageOld, 8, 8);
      int[] colour = getAverageColour(image);

      try {
         is.close();
      } catch (IOException var8) {
      }

      return colour;
   }

   public static int[] getMostCommonColour(BufferedImage image) {
      int height = image.getHeight();
      int width = image.getWidth();
      Map map = new HashMap();

      for(int i = 0; i < width; ++i) {
         for(int j = 0; j < height; ++j) {
            int rgb = image.getRGB(i, j);
            int[] rgbArr = getRGBArr(rgb);
            if (rgbArr[3] != 0) {
               Integer counter = (Integer)map.get(rgb);
               if (counter == null) {
                  counter = 0;
               }

               ++counter;
               map.put(rgb, counter);
            }
         }
      }

      List<?> list = new LinkedList(map.entrySet());
      Collections.sort(list, new Comparator() {
         public int compare(Object o1, Object o2) {
            return ((Comparable)((Map.Entry)((Map.Entry)o1)).getValue()).compareTo(((Map.Entry)((Map.Entry)o2)).getValue());
         }
      });
      Map.Entry me = (Map.Entry)list.get(list.size() - 1);
      int[] rgb = getRGBArr((Integer)me.getKey());
      return rgb;
   }

   public static int[] getAverageColour(BufferedImage image) {
      int height = image.getHeight();
      int width = image.getWidth();
      List<int[]> list = new ArrayList();

      for(int i = 0; i < width; ++i) {
         for(int j = 0; j < height; ++j) {
            int rgb = image.getRGB(i, j);
            int[] rgbArr = getRGBArr(rgb);
            if (rgbArr[3] != 0) {
               list.add(rgbArr);
            }
         }
      }

      if (list != null && list.size() > 0) {
         int totalRed = 0;
         int totalGreen = 0;
         int totalBlue = 0;

         for(int[] color : list) {
            totalRed += color[0];
            totalGreen += color[1];
            totalBlue += color[2];
         }

         int avgRed = totalRed / list.size();
         int avgGreen = totalGreen / list.size();
         int avgBlue = totalBlue / list.size();
         return new int[]{avgRed, avgGreen, avgBlue, 1};
      } else {
         return new int[]{0, 0, 0, 255};
      }
   }

   public static int[] getRGBArr(int pixel) {
      int alpha = pixel >> 24 & 255;
      int red = pixel >> 16 & 255;
      int green = pixel >> 8 & 255;
      int blue = pixel & 255;
      return new int[]{red, green, blue, alpha};
   }

   private static BufferedImage getScaledImage(BufferedImage src, int w, int h) {
      int finalw = w;
      int finalh = h;
      double factor = (double)1.0F;
      if (src.getWidth() > src.getHeight()) {
         factor = (double)src.getHeight() / (double)src.getWidth();
         finalh = (int)((double)w * factor);
      } else {
         factor = (double)src.getWidth() / (double)src.getHeight();
         finalw = (int)((double)h * factor);
      }

      if (finalw <= 0) {
         finalw = w;
      }

      if (finalh <= 0) {
         finalh = h;
      }

      BufferedImage resizedImg = new BufferedImage(finalw, finalh, 3);
      Graphics2D g2 = resizedImg.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2.drawImage(src, 0, 0, finalw, finalh, (ImageObserver)null);
      g2.dispose();
      return resizedImg;
   }

   public static BufferedImage getBufferedImage(TextureAtlasSprite textureAtlasSprite) {
      int iconWidth = textureAtlasSprite.contents().width();
      int iconHeight = textureAtlasSprite.contents().height();
      if (iconWidth > 0 && iconHeight > 0) {
         BufferedImage bufferedImage = new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_4BYTE_ABGR);
         return bufferedImage;
      } else {
         return null;
      }
   }
}
