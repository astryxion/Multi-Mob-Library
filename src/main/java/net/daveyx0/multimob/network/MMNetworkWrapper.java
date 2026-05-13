package net.daveyx0.multimob.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;

public class MMNetworkWrapper {
   public MMNetworkWrapper() {
   }

   public static void sendPacket(Entity player, Packet<?> packet) {
      if (player instanceof ServerPlayer && ((ServerPlayer)player).connection != null) {
         ((ServerPlayer)player).connection.send(packet);
      }

   }

   public void sendToAll(CustomPacketPayload payload, CustomPacketPayload... payloads) {
      PacketDistributor.sendToAllPlayers(payload, payloads);
   }

   public void sendToNear(ServerLevel level, double x, double y, double z, double radius, CustomPacketPayload payload, CustomPacketPayload... payloads) {
      PacketDistributor.sendToPlayersNear(level, null, x, y, z, radius, payload, payloads);
   }
}
