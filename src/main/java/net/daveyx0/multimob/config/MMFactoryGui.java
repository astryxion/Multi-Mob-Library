package net.daveyx0.multimob.config;

import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MMFactoryGui {
   public static BiFunction<Minecraft, Screen, Screen> getFactory() {
      return (mc, parentScreen) -> new MMGuiConfig(parentScreen);
   }
}
