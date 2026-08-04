package net.daveyx0.multimob.message;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MessageMMParticle(int id, int amount, int block, float x, float y, float z, double xVel, double yVel, double zVel)
      implements CustomPacketPayload {

   public static final CustomPacketPayload.Type<MessageMMParticle> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("multimob", "particle"));

   public static final StreamCodec<RegistryFriendlyByteBuf, MessageMMParticle> STREAM_CODEC = StreamCodec.of(
      (buf, msg) -> {
         buf.writeInt(msg.id);
         buf.writeInt(msg.amount);
         buf.writeInt(msg.block);
         buf.writeFloat(msg.x);
         buf.writeFloat(msg.y);
         buf.writeFloat(msg.z);
         buf.writeDouble(msg.xVel);
         buf.writeDouble(msg.yVel);
         buf.writeDouble(msg.zVel);
      },
      buf -> new MessageMMParticle(
         buf.readInt(), buf.readInt(), buf.readInt(),
         buf.readFloat(), buf.readFloat(), buf.readFloat(),
         buf.readDouble(), buf.readDouble(), buf.readDouble()
      )
   );

   public MessageMMParticle(int particleId, int amount, float x, float y, float z, double xVelocity, double yVelocity, double zVelocity, int blockID) {
      this(particleId, amount, blockID, x, y, z, xVelocity, yVelocity, zVelocity);
   }

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(MessageMMParticle message, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (message.amount > 0 && Minecraft.getInstance().level != null) {
            for (int i = 0; i < message.amount; ++i) {
               Random rand = new Random();
               SimpleParticleType particleType = getParticleById(message.id);
               if (particleType != null) {
                  Minecraft.getInstance().level.addParticle(particleType,
                     (double)(message.x + (rand.nextFloat() - rand.nextFloat())),
                     (double)message.y,
                     (double)(message.z + (rand.nextFloat() - rand.nextFloat())),
                     message.xVel, message.yVel, message.zVel);
               }
            }
         }
      });
   }

   private static SimpleParticleType getParticleById(int id) {
      return switch (id) {
         case 0 -> ParticleTypes.EXPLOSION;
         case 1 -> ParticleTypes.EXPLOSION_EMITTER;
         case 2 -> ParticleTypes.FIREWORK;
         case 3 -> ParticleTypes.BUBBLE;
         case 4 -> ParticleTypes.SPLASH;
         case 5 -> ParticleTypes.FISHING;
         case 6 -> ParticleTypes.UNDERWATER;
         case 7 -> ParticleTypes.CRIT;
         case 8 -> ParticleTypes.ENCHANTED_HIT;
         case 9 -> ParticleTypes.SMOKE;
         case 10 -> ParticleTypes.LARGE_SMOKE;
         case 11, 12, 13, 14 -> ParticleTypes.EFFECT;
         case 15 -> ParticleTypes.WITCH;
         case 16 -> ParticleTypes.DRIPPING_WATER;
         case 17 -> ParticleTypes.DRIPPING_LAVA;
         case 18 -> ParticleTypes.ANGRY_VILLAGER;
         case 19 -> ParticleTypes.HAPPY_VILLAGER;
         case 20 -> ParticleTypes.MYCELIUM;
         case 21 -> ParticleTypes.NOTE;
         case 22 -> ParticleTypes.PORTAL;
         case 23 -> ParticleTypes.ENCHANT;
         case 24 -> ParticleTypes.FLAME;
         case 25 -> ParticleTypes.LAVA;
         case 26 -> ParticleTypes.CLOUD;
         case 29 -> ParticleTypes.HEART;
         case 30 -> ParticleTypes.ASH;
         case 33 -> ParticleTypes.RAIN;
         case 35 -> ParticleTypes.ITEM_SNOWBALL;
         case 38 -> ParticleTypes.DOLPHIN;
         case 40 -> ParticleTypes.SNOWFLAKE;
         case 41 -> ParticleTypes.DRAGON_BREATH;
         case 42 -> ParticleTypes.END_ROD;
         case 43 -> ParticleTypes.DAMAGE_INDICATOR;
         case 44 -> ParticleTypes.SWEEP_ATTACK;
         case 46 -> ParticleTypes.TOTEM_OF_UNDYING;
         case 47 -> ParticleTypes.SPIT;
         default -> ParticleTypes.SMOKE;
      };
   }
}
