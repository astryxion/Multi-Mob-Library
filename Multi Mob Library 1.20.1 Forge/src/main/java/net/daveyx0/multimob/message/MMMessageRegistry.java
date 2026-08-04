package net.daveyx0.multimob.message;

import net.daveyx0.multimob.network.MMNetworkWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class MMMessageRegistry {
   private static int id = 0;
   public static MMNetworkWrapper networkWrapper;
   private static final String PROTOCOL_VERSION = "1";

   public static void preInit() {
      SimpleChannel channel = NetworkRegistry.newSimpleChannel(
         new ResourceLocation("multimob", "main"),
         () -> PROTOCOL_VERSION,
         PROTOCOL_VERSION::equals,
         PROTOCOL_VERSION::equals
      );
      networkWrapper = new MMNetworkWrapper(channel);
      registerMessages();
   }

   public static void registerMessages() {
      networkWrapper.network.registerMessage(id++, MessageMMParticle.class, MessageMMParticle::encode, MessageMMParticle::decode, MessageMMParticle::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
      networkWrapper.network.registerMessage(id++, MessageMMTameable.class, MessageMMTameable::encode, MessageMMTameable::decode, MessageMMTameable::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
      networkWrapper.network.registerMessage(id++, MessageMMVariant.class, MessageMMVariant::encode, MessageMMVariant::decode, MessageMMVariant::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
   }

   public static SimpleChannel getNetwork() {
      return networkWrapper.network;
   }

   public static MMNetworkWrapper getWrapper() {
      return networkWrapper;
   }
}
