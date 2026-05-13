package net.daveyx0.multimob.message;

import java.util.UUID;
import net.daveyx0.multimob.common.capabilities.CapabilityTameableEntity;
import net.daveyx0.multimob.common.capabilities.ITameableEntity;
import net.daveyx0.multimob.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageMMTameable implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<MessageMMTameable> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("multimob", "tameable"));
   public static final StreamCodec<RegistryFriendlyByteBuf, MessageMMTameable> STREAM_CODEC = StreamCodec.ofMember(MessageMMTameable::encode, MessageMMTameable::decode);
   private String entityId;
   private String ownerId;
   private int followState;

   public MessageMMTameable() {
   }

   public MessageMMTameable(String entityInID, String summonerInID, int followState) {
      this.entityId = entityInID;
      this.ownerId = summonerInID;
      this.followState = followState;
   }

   public static MessageMMTameable decode(FriendlyByteBuf buf) {
      MessageMMTameable msg = new MessageMMTameable();
      msg.entityId = buf.readUtf();
      msg.ownerId = buf.readUtf();
      msg.followState = buf.readInt();
      return msg;
   }

   public static void encode(MessageMMTameable msg, FriendlyByteBuf buf) {
      buf.writeUtf(msg.entityId);
      buf.writeUtf(msg.ownerId);
      buf.writeInt(msg.followState);
   }

   public static void handle(MessageMMTameable message, IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         if (FMLEnvironment.dist.isClient()) {
            if (!message.entityId.isEmpty() && !message.ownerId.isEmpty()) {
               LivingEntity entity = EntityUtil.getLoadedEntityByUUID(UUID.fromString(message.entityId), Minecraft.getInstance().level);
               if (entity != null) {
                  ITameableEntity tameable = entity.getCapability(CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY);
                  if (tameable != null) {
                     tameable.setTamed(true);
                     tameable.setOwner(UUID.fromString(message.ownerId));
                     tameable.setFollowState(message.followState);
                     CompoundTag nbttagcompound = entity.saveWithoutId(new CompoundTag());
                     nbttagcompound.putString("Owner", message.ownerId);
                     nbttagcompound.putString("OwnerUUID", message.ownerId);
                     nbttagcompound.putBoolean("Tame", true);
                     nbttagcompound.putBoolean("Tamed", true);
                     entity.load(nbttagcompound);
                  }
               }
            }
         }
      });
   }

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
