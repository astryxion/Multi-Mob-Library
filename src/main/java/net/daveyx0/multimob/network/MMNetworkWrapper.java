package net.daveyx0.multimob.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.simple.SimpleChannel;

public class MMNetworkWrapper {
   public final SimpleChannel network;

   public MMNetworkWrapper(SimpleChannel network) {
      this.network = network;
   }

   public static void sendPacket(Entity player, Packet<?> packet) {
      if (player instanceof ServerPlayer && ((ServerPlayer)player).connection != null) {
         ((ServerPlayer)player).connection.send(packet);
      }

   }
}
