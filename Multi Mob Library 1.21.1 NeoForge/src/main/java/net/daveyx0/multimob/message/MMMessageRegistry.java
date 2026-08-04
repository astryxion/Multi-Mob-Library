package net.daveyx0.multimob.message;

import net.daveyx0.multimob.network.MMNetworkWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class MMMessageRegistry {
   public static MMNetworkWrapper networkWrapper = new MMNetworkWrapper();
   private static final String PROTOCOL_VERSION = "1";

   public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("multimob").versioned(PROTOCOL_VERSION);
      registrar.playToClient(MessageMMParticle.TYPE, MessageMMParticle.STREAM_CODEC, MessageMMParticle::handle);
      registrar.playToClient(MessageMMTameable.TYPE, MessageMMTameable.STREAM_CODEC, MessageMMTameable::handle);
      registrar.playToClient(MessageMMVariant.TYPE, MessageMMVariant.STREAM_CODEC, MessageMMVariant::handle);
      networkWrapper = new MMNetworkWrapper();
   }

   /** @deprecated Use registerPayloads via mod bus; kept for API compatibility. */
   @Deprecated
   public static void preInit() {
   }

   public static MMNetworkWrapper getNetwork() {
      return networkWrapper;
   }

   public static MMNetworkWrapper getWrapper() {
      return networkWrapper;
   }
}
