package net.daveyx0.multimob.config;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@OnlyIn(Dist.CLIENT)
public class MMFactoryGui {
   public static IConfigScreenFactory getFactory() {
      return (container, parentScreen) -> new MMGuiConfig(parentScreen);
   }
}
