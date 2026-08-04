package net.daveyx0.multimob.client;

import javax.annotation.Nullable;
import net.daveyx0.multimob.client.renderer.MMRenderManager;
import net.daveyx0.multimob.common.MMCommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MMClientProxy extends MMCommonProxy {
   private final Minecraft MINECRAFT = Minecraft.getInstance();

   @Override
   public void clientSetup() {
      MMRenderManager.init();
   }

   @Nullable
   @Override
   public Player getClientPlayer() {
      return this.MINECRAFT.player;
   }

   @Nullable
   @Override
   public Level getClientWorld() {
      return this.MINECRAFT.level;
   }

   @Override
   public Player getPlayer(IPayloadContext context) {
      if (context.player() != null) {
         return context.player();
      }
      return this.MINECRAFT.player;
   }
}
