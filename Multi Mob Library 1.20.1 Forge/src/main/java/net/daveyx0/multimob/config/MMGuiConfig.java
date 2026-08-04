package net.daveyx0.multimob.config;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MMGuiConfig extends Screen {
   private final Screen parentScreen;

   public MMGuiConfig(Screen parentScreen) {
      super(Component.translatable("multimob.config.title"));
      this.parentScreen = parentScreen;
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.parentScreen);
   }
}
