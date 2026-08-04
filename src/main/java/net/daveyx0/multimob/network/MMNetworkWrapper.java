package net.daveyx0.multimob.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;

public class MMNetworkWrapper {

   public MMNetworkWrapper() {
   }

   public void sendToTracking(Entity entity, CustomPacketPayload payload) {
      PacketDistributor.sendToPlayersTrackingEntity(entity, payload);
   }

   public void sendToNearby(Entity entity, double range, CustomPacketPayload payload) {
      if (entity.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
         PacketDistributor.sendToPlayersNear(serverLevel, null, entity.getX(), entity.getY(), entity.getZ(), range, payload);
      }
   }

   public void sendToAll(CustomPacketPayload payload) {
      PacketDistributor.sendToAllPlayers(payload);
   }

   public void send(CustomPacketPayload payload, ServerPlayer player) {
      PacketDistributor.sendToPlayer(player, payload);
   }

   public static void sendPacket(Entity player, net.minecraft.network.protocol.Packet<?> packet) {
      if (player instanceof ServerPlayer serverPlayer && serverPlayer.connection != null) {
         serverPlayer.connection.send(packet);
      }
   }
}
