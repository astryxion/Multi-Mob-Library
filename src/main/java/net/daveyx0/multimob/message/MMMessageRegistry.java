package net.daveyx0.multimob.message;

import net.daveyx0.multimob.network.MMNetworkWrapper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class MMMessageRegistry {
   private static int id = 0;
   public static MMNetworkWrapper networkWrapper;
   private static final String PROTOCOL_VERSION = "1";

   public static void preInit() {
      networkWrapper = new MMNetworkWrapper();
   }

   @SubscribeEvent
   public static void registerPayloads(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
      registrar.playToClient(MessageMMParticle.TYPE, MessageMMParticle.STREAM_CODEC, MessageMMParticle::handle);
      registrar.playToClient(MessageMMTameable.TYPE, MessageMMTameable.STREAM_CODEC, MessageMMTameable::handle);
      registrar.playToClient(MessageMMVariant.TYPE, MessageMMVariant.STREAM_CODEC, MessageMMVariant::handle);
   }

   public static void registerMessages() {
   }

   public static MMNetworkWrapper getNetwork() {
      return networkWrapper;
   }

   public static MMNetworkWrapper getWrapper() {
      return networkWrapper;
   }
}
