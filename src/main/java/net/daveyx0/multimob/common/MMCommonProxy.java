package net.daveyx0.multimob.common;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MMCommonProxy {

   public void commonSetup() {
   }

   public void clientSetup() {
   }

   @Nullable
   public Player getClientPlayer() {
      throw new RuntimeException("Tried to get the client player on the dedicated server");
   }

   @Nullable
   public Level getClientWorld() {
      throw new RuntimeException("Tried to get the client world on the dedicated server");
   }

   public Player getPlayer(IPayloadContext context) {
      if (context.player() != null) {
         return context.player();
      }
      throw new RuntimeException("Tried to get the player from a client-side MessageContext on the dedicated server");
   }
}
