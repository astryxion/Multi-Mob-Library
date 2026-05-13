package net.daveyx0.multimob.common;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

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

   public Player getPlayer(Supplier<IPayloadContext> context) {
      if (context.get().flow().isServerbound()) {
         return context.get().player();
      } else {
         throw new RuntimeException("Tried to get the player from a client-side MessageContext on the dedicated server");
      }
   }
}
