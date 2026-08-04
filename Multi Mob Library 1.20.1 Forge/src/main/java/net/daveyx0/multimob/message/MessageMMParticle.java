package net.daveyx0.multimob.message;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class MessageMMParticle {
   int id;
   int amount;
   int block;
   float x;
   float y;
   float z;
   double xVel;
   double yVel;
   double zVel;

   public MessageMMParticle() {
   }

   public MessageMMParticle(int particleId, int amount, float x, float y, float z, double xVelocity, double yVelocity, double zVelocity, int blockID) {
      this.id = particleId;
      this.amount = amount;
      this.block = blockID;
      this.x = x;
      this.y = y;
      this.z = z;
      this.xVel = xVelocity;
      this.yVel = yVelocity;
      this.zVel = zVelocity;
   }

   public static MessageMMParticle decode(FriendlyByteBuf buf) {
      MessageMMParticle msg = new MessageMMParticle();
      msg.id = buf.readInt();
      msg.amount = buf.readInt();
      msg.block = buf.readInt();
      msg.x = buf.readFloat();
      msg.y = buf.readFloat();
      msg.z = buf.readFloat();
      msg.xVel = buf.readDouble();
      msg.yVel = buf.readDouble();
      msg.zVel = buf.readDouble();
      return msg;
   }

   public static void encode(MessageMMParticle msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
      buf.writeInt(msg.amount);
      buf.writeInt(msg.block);
      buf.writeFloat(msg.x);
      buf.writeFloat(msg.y);
      buf.writeFloat(msg.z);
      buf.writeDouble(msg.xVel);
      buf.writeDouble(msg.yVel);
      buf.writeDouble(msg.zVel);
   }

   public static void handle(MessageMMParticle message, Supplier<NetworkEvent.Context> ctx) {
      ctx.get().enqueueWork(() -> {
         DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
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
      });
      ctx.get().setPacketHandled(true);
   }

   private static SimpleParticleType getParticleById(int id) {
      switch (id) {
         case 0: return ParticleTypes.EXPLOSION;
         case 1: return ParticleTypes.EXPLOSION_EMITTER;
         case 2: return ParticleTypes.FIREWORK;
         case 3: return ParticleTypes.BUBBLE;
         case 4: return ParticleTypes.SPLASH;
         case 5: return ParticleTypes.FISHING;
         case 6: return ParticleTypes.UNDERWATER;
         case 7: return ParticleTypes.CRIT;
         case 8: return ParticleTypes.ENCHANTED_HIT;
         case 9: return ParticleTypes.SMOKE;
         case 10: return ParticleTypes.LARGE_SMOKE;
         case 11: return ParticleTypes.EFFECT;
         case 12: return ParticleTypes.INSTANT_EFFECT;
         case 13: return ParticleTypes.ENTITY_EFFECT;
         case 14: return ParticleTypes.AMBIENT_ENTITY_EFFECT;
         case 15: return ParticleTypes.WITCH;
         case 16: return ParticleTypes.DRIPPING_WATER;
         case 17: return ParticleTypes.DRIPPING_LAVA;
         case 18: return ParticleTypes.ANGRY_VILLAGER;
         case 19: return ParticleTypes.HAPPY_VILLAGER;
         case 20: return ParticleTypes.MYCELIUM;
         case 21: return ParticleTypes.NOTE;
         case 22: return ParticleTypes.PORTAL;
         case 23: return ParticleTypes.ENCHANT;
         case 24: return ParticleTypes.FLAME;
         case 25: return ParticleTypes.LAVA;
         case 26: return ParticleTypes.CLOUD;
         case 29: return ParticleTypes.HEART;
         case 30: return ParticleTypes.ASH;
         case 33: return ParticleTypes.RAIN;
         case 35: return ParticleTypes.ITEM_SNOWBALL;
         case 38: return ParticleTypes.DOLPHIN;
         case 40: return ParticleTypes.SNOWFLAKE;
         case 41: return ParticleTypes.DRAGON_BREATH;
         case 42: return ParticleTypes.END_ROD;
         case 43: return ParticleTypes.DAMAGE_INDICATOR;
         case 44: return ParticleTypes.SWEEP_ATTACK;
         case 46: return ParticleTypes.TOTEM_OF_UNDYING;
         case 47: return ParticleTypes.SPIT;
         default: return ParticleTypes.SMOKE;
      }
   }
}
