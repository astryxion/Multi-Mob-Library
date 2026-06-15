package net.daveyx0.multimob.common.capabilities;

import java.util.UUID;
import net.daveyx0.multimob.capabilities.CapabilityProviderSerializable;
import net.daveyx0.multimob.core.MMTameableEntries;
import net.daveyx0.multimob.entity.ai.EntityAITameableFollowOwner;
import net.daveyx0.multimob.entity.ai.EntityAITameableOwnerHurtByTarget;
import net.daveyx0.multimob.entity.ai.EntityAITameableOwnerHurtTarget;
import net.daveyx0.multimob.message.MMMessageRegistry;
import net.daveyx0.multimob.message.MessageMMParticle;
import net.daveyx0.multimob.message.MessageMMTameable;
import net.daveyx0.multimob.util.EntityUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

public class CapabilityTameableEntity {
   public static Capability<ITameableEntity> TAMEABLE_ENTITY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
   public static final ResourceLocation capabilityID = new ResourceLocation("multimob", "tameable");

   public static void register() {
   }

   @Mod.EventBusSubscriber(modid = "multimob")
   public static class EventHandler {
      @SubscribeEvent
      public static void AttachEntityCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
         Entity entity = event.getObject();
         if (entity != null) {
            for (Class<? extends Entity> tameableClass : MMTameableEntries.tameableEntries.keySet()) {
               if (tameableClass.isInstance(entity)) {
                  event.addCapability(CapabilityTameableEntity.capabilityID, new CapabilityProviderSerializable<>(CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null, new TameableEntityHandler()));
                  return;
               }
            }
         }
      }

      @SubscribeEvent
      public static void EntityLivingDeathEvent(LivingDeathEvent event) {
         if (isTameableEntity(event.getEntity())) {
            ITameableEntity tameable = (ITameableEntity)EntityUtil.getCapability(event.getEntity(), CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, (Direction)null);
            Mob entity = (Mob)event.getEntity();
            if (!entity.level().isClientSide && entity.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_SHOWDEATHMESSAGES) && tameable.getOwner(entity) != null && tameable.getOwner(entity) instanceof ServerPlayer) {
               tameable.getOwner(entity).sendSystemMessage(entity.getCombatTracker().getDeathMessage());
            }
         }
      }

      @SubscribeEvent
      public static void JoinWorldEvent(EntityJoinLevelEvent event) {
         if (isTameableEntity(event.getEntity())) {
            Mob entity = (Mob)event.getEntity();
            ITameableEntity tameable = (ITameableEntity)EntityUtil.getCapability(event.getEntity(), CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, (Direction)null);
            if (tameable != null && tameable.isTamed()) {
               if (tameable.getFollowState() == 0 || entity.isBaby()) {
                  resetEntityTargetAI(entity);
               } else {
                  updateEntityTargetAI(entity);
               }

               entity.goalSelector.getAvailableGoals().stream().filter((taskEntry) -> taskEntry.getGoal() instanceof EntityAITameableFollowOwner).findFirst().ifPresent((taskEntry) -> entity.goalSelector.removeGoal(taskEntry.getGoal()));
               if (tameable.getFollowState() == 2) {
                  entity.goalSelector.addGoal(3, new EntityAITameableFollowOwner(entity, 1.2, 8.0F, 2.0F));
               }

               if (MMTameableEntries.tameableEntries.containsKey(entity.getClass())) {
                  TameableEntityEntry entry = MMTameableEntries.tameableEntries.get(entity.getClass());
                  entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)entry.getTamedHealth());
               }
            }
         }
      }

      @SubscribeEvent
      public static void EntityUpdateEvent(LivingEvent.LivingTickEvent event) {
         if (!isTameableEntity(event.getEntity())) {
            return;
         }

         Mob entity = (Mob)event.getEntity();
         ITameableEntity tameable = (ITameableEntity)EntityUtil.getCapability(event.getEntity(), CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, (Direction)null);
         if (tameable == null || !tameable.isTamed()) {
            return;
         }

         if (tameable.getFollowState() == 0) {
            entity.getNavigation().stop();
            entity.setSpeed(0.0F);
            entity.setTarget((LivingEntity)null);
         }
      }

      @SubscribeEvent
      public static void EntityDamageEvent(LivingDamageEvent event) {
         if (isTameableEntity(event.getEntity())) {
            Mob entity = (Mob)event.getEntity();
            ITameableEntity tameable = (ITameableEntity)EntityUtil.getCapability(event.getEntity(), CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, (Direction)null);
            if (tameable != null && tameable.isTamed() && (event.getSource() == entity.damageSources().fellOutOfWorld() || event.getSource() == entity.damageSources().drown() || event.getSource() == entity.damageSources().inFire())) {
               event.setResult(Event.Result.DENY);
            }
         }
      }

      @SubscribeEvent
      public static void PlayerStartsTrackingEvent(PlayerEvent.StartTracking event) {
         if (!event.getEntity().level().isClientSide && isTameableEntity(event.getTarget())) {
            ITameableEntity tameable = (ITameableEntity)EntityUtil.getCapability(event.getTarget(), CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, (Direction)null);
            if (tameable.getOwnerId() != null) {
               MMMessageRegistry.getNetwork().send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(event.getTarget().getX(), event.getTarget().getY(), event.getTarget().getZ(), (double)255.0F, event.getEntity().level().dimension())), new MessageMMTameable(event.getTarget().getUUID().toString(), tameable.getOwnerId().toString(), tameable.getFollowState()));
            }
         }
      }

      @SubscribeEvent
      public static void PlayerInteractEvent(PlayerInteractEvent.EntityInteract event) {
         if (isTameableEntity(event.getTarget()) && event.getHand() == InteractionHand.MAIN_HAND) {
            Mob entity = (Mob)event.getTarget();
            ITameableEntity tameable = (ITameableEntity)EntityUtil.getCapability(event.getTarget(), CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, (Direction)null);
            if (tameable != null) {
               if (tameable.isTamed() && tameable.getOwner(entity) == event.getEntity()) {
                  if (event.getItemStack() == ItemStack.EMPTY) {
                     if (event.getEntity().isShiftKeyDown()) {
                        tameable.setFollowState(tameable.getFollowState() + 1);
                        if (tameable.getFollowState() == 3) {
                           tameable.setFollowState(0);
                        }

                        MMMessageRegistry.getNetwork().send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(event.getTarget().getX(), event.getTarget().getY(), event.getTarget().getZ(), (double)255.0F, event.getEntity().level().dimension())), new MessageMMTameable(event.getTarget().getUUID().toString(), tameable.getOwnerId().toString(), tameable.getFollowState()));
                        if (tameable.getFollowState() == 2) {
                           entity.goalSelector.addGoal(3, new EntityAITameableFollowOwner(entity, 1.2, 8.0F, 2.0F));
                        } else {
                           entity.goalSelector.getAvailableGoals().stream().filter((taskEntry) -> taskEntry.getGoal() instanceof EntityAITameableFollowOwner).findFirst().ifPresent((taskEntry) -> entity.goalSelector.removeGoal(taskEntry.getGoal()));
                        }

                        if (tameable.getFollowState() == 0) {
                           resetEntityTargetAI(entity);
                        } else {
                           updateEntityTargetAI(entity);
                        }

                        if (!event.getEntity().level().isClientSide) {
                           int particleID = 0;
                           if (tameable.getFollowState() == 0) {
                              particleID = 19;
                              event.getEntity().sendSystemMessage(Component.translatable("%1$s is now sitting.", new Object[]{event.getTarget().getDisplayName()}));
                           } else if (tameable.getFollowState() == 1) {
                              particleID = 21;
                              event.getEntity().sendSystemMessage(Component.translatable("%1$s is now wandering.", new Object[]{event.getTarget().getDisplayName()}));
                           } else if (tameable.getFollowState() == 2) {
                              particleID = 29;
                              event.getEntity().sendSystemMessage(Component.translatable("%1$s is now following.", new Object[]{event.getTarget().getDisplayName()}));
                           }

                           MMMessageRegistry.getNetwork().send(PacketDistributor.ALL.noArg(), new MessageMMParticle(particleID, 15, (float)entity.getX() + 0.5F, (float)entity.getY() + 0.5F, (float)entity.getZ() + 0.5F, (double)0.0F, (double)0.0F, (double)0.0F, 0));
                        }
                     } else if (entity.canBeLeashed(event.getEntity()) && !event.getEntity().level().isClientSide) {
                        entity.setLeashedTo(event.getEntity(), true);
                     }
                  } else if (MMTameableEntries.tameableEntries.containsKey(entity.getClass())) {
                     TameableEntityEntry entry = (TameableEntityEntry)MMTameableEntries.tameableEntries.get(entity.getClass());
                     if (entry.getHealItems() != null && entry.getHealItems().length > 0) {
                        for(Item item : entry.getHealItems()) {
                           if (event.getItemStack().getItem() == item) {
                              if (!event.getEntity().getAbilities().instabuild) {
                                 event.getItemStack().shrink(1);
                              }

                              entity.heal(10.0F);
                              playHealEffect(entity);
                              break;
                           }
                        }
                     }
                  }
               } else if (!tameable.isTamed() && event.getItemStack() != null && MMTameableEntries.tameableEntries.containsKey(entity.getClass())) {
                  TameableEntityEntry entry = (TameableEntityEntry)MMTameableEntries.tameableEntries.get(entity.getClass());
                  if (entry.getTameItems() != null && entry.getCanBeTamedWithItem() && entry.getTameItems().length > 0) {
                     for(Item item : entry.getTameItems()) {
                        if (event.getItemStack().getItem() == item) {
                           if (!event.getEntity().getAbilities().instabuild) {
                              event.getItemStack().shrink(1);
                           }

                           setUpTameable(tameable, entity, event.getEntity());
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      public static void setUpTameable(ITameableEntity tameable, Mob entity, LivingEntity owner) {
         tameable.setOwner(owner.getUUID());
         tameable.setTamed(true);
         tameable.setFollowState(2);
         entity.setTarget(null);
         MMMessageRegistry.getNetwork().send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(entity.getX(), entity.getY(), entity.getZ(), (double)255.0F, owner.level().dimension())), new MessageMMTameable(entity.getUUID().toString(), tameable.getOwnerId().toString(), tameable.getFollowState()));
         if (entity.isBaby()) {
            resetEntityTargetAI(entity);
         } else {
            updateEntityTargetAI(entity);
         }
         entity.goalSelector.addGoal(3, new EntityAITameableFollowOwner(entity, 1.2, 8.0F, 2.0F));
         playHealEffect(entity);
      }

      @SubscribeEvent
      public static void EntityDespawnEvent(MobSpawnEvent.AllowDespawn event) {
         if (isTameableEntity(event.getEntity())) {
            ITameableEntity tameable = (ITameableEntity)EntityUtil.getCapability(event.getEntity(), CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, (Direction)null);
            if (tameable != null && tameable.isTamed()) {
               event.setResult(Event.Result.DENY);
            }
         }
      }

      public static void updateEntityTargetAI(Mob base) {
         while(base.targetSelector.getAvailableGoals().stream().filter((taskEntry) -> taskEntry.getGoal() instanceof Goal).findFirst().isPresent()) {
            base.targetSelector.getAvailableGoals().stream().filter((taskEntry) -> taskEntry.getGoal() instanceof Goal).findFirst().ifPresent((taskEntry) -> base.targetSelector.removeGoal(taskEntry.getGoal()));
         }

         base.targetSelector.addGoal(0, new EntityAITameableOwnerHurtByTarget(base));
         base.targetSelector.addGoal(1, new EntityAITameableOwnerHurtTarget(base));
      }

      public static void resetEntityTargetAI(Mob base) {
         while(base.targetSelector.getAvailableGoals().stream().filter((taskEntry) -> taskEntry.getGoal() instanceof Goal).findFirst().isPresent()) {
            base.targetSelector.getAvailableGoals().stream().filter((taskEntry) -> taskEntry.getGoal() instanceof Goal).findFirst().ifPresent((taskEntry) -> base.targetSelector.removeGoal(taskEntry.getGoal()));
         }
      }

      public static boolean isTameableEntity(Entity entity) {
         return entity != null && entity instanceof Mob && entity.getCapability(CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY).isPresent();
      }

      protected static void playHealEffect(Entity entity) {
         for(int i = 0; i < 7; ++i) {
            double d0 = entity.level().random.nextGaussian() * 0.02;
            double d1 = entity.level().random.nextGaussian() * 0.02;
            double d2 = entity.level().random.nextGaussian() * 0.02;
            double x = entity.getX() + (double)(entity.level().random.nextFloat() * entity.getBbWidth() * 2.0F) - (double)entity.getBbWidth();
            double y = entity.getY() + (double)0.5F + (double)(entity.level().random.nextFloat() * entity.getBbHeight());
            double z = entity.getZ() + (double)(entity.level().random.nextFloat() * entity.getBbWidth() * 2.0F) - (double)entity.getBbWidth();
            if (entity.level() instanceof ServerLevel serverLevel) {
               serverLevel.sendParticles(ParticleTypes.HEART, x, y, z, 1, d0, d1, d2, 0.0D);
            } else {
               entity.level().addParticle(ParticleTypes.HEART, x, y, z, d0, d1, d2);
            }
         }
      }
   }
}
