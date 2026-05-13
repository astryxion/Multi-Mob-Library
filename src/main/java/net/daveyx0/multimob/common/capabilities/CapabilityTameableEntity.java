package net.daveyx0.multimob.common.capabilities;

import java.util.UUID;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.MobDespawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.EntityCapability;

public class CapabilityTameableEntity {
   public static final EntityCapability<ITameableEntity, Void> TAMEABLE_ENTITY_CAPABILITY = EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath("multimob", "tameable"), ITameableEntity.class);
   public static final ResourceLocation capabilityID = ResourceLocation.fromNamespaceAndPath("multimob", "tameable");

   public static void register() {
   }

   @EventBusSubscriber(modid = "multimob")
   public static class EventHandler {
      @SubscribeEvent
      public static void EntityLivingDeathEvent(LivingDeathEvent event) {
         if (isTameableEntity(event.getEntity())) {
            ITameableEntity tameable = EntityUtil.getCapability(event.getEntity(), CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, (Direction)null);
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
            ITameableEntity tameable = EntityUtil.getCapability(event.getEntity(), CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, (Direction)null);
            if (tameable != null && tameable.isTamed()) {
               if (tameable.getFollowState() == 0 || tameable.getFollowState() == 0) {
                  resetEntityTargetAI(entity);
               } else {
                  updateEntityTargetAI(entity);
               }

               entity.goalSelector.getAvailableGoals().stream().filter((taskEntry) -> taskEntry.getGoal() instanceof EntityAITameableFollowOwner).findFirst().ifPresent((taskEntry) -> entity.goalSelector.removeGoal(taskEntry.getGoal()));
               if (tameable.getFollowState() == 2) {
                  entity.goalSelector.addGoal(3, new EntityAITameableFollowOwner(entity, 1.2, 8.0F, 2.0F));
               }

               if (MMTameableEntries.tameableEntries.containsKey(entity)) {
                  TameableEntityEntry entry = (TameableEntityEntry)MMTameableEntries.tameableEntries.get(entity);
                  entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)entry.getTamedHealth());
               }
            }
         }
      }

      @SubscribeEvent
      public static void EntityUpdateEvent(EntityTickEvent.Post event) {
         if (isTameableEntity(event.getEntity())) {
            Mob entity = (Mob)event.getEntity();
            ITameableEntity tameable = EntityUtil.getCapability(event.getEntity(), CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, (Direction)null);
            if (tameable != null && tameable.isTamed()) {
               if (tameable.getFollowState() == 0) {
                  entity.getNavigation().stop();
                  entity.setSpeed(0.0F);
                  entity.setTarget((LivingEntity)null);
               }

               if (entity.level().random.nextInt(200) == 0) {
                  entity.level().addParticle(ParticleTypes.HEART, entity.getX() + (double)(entity.level().random.nextFloat() - entity.level().random.nextFloat()), entity.getY() + (double)entity.level().random.nextFloat() + (double)1.0F, entity.getZ() + (double)(entity.level().random.nextFloat() - entity.level().random.nextFloat()), (double)1.0F, (double)1.0F, (double)1.0F);
               }
            }
         }
      }

      @SubscribeEvent
      public static void EntityDamageEvent(LivingDamageEvent.Pre event) {
         if (isTameableEntity(event.getEntity())) {
            Mob entity = (Mob)event.getEntity();
            ITameableEntity tameable = EntityUtil.getCapability(event.getEntity(), CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, (Direction)null);
            if (tameable != null && tameable.isTamed() && (event.getSource() == entity.damageSources().fellOutOfWorld() || event.getSource() == entity.damageSources().drown() || event.getSource() == entity.damageSources().inFire())) {
               event.setNewDamage(0.0F);
            }
         }
      }

      @SubscribeEvent
      public static void PlayerStartsTrackingEvent(PlayerEvent.StartTracking event) {
         if (!event.getEntity().level().isClientSide && isTameableEntity(event.getTarget())) {
            ITameableEntity tameable = EntityUtil.getCapability(event.getTarget(), CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, (Direction)null);
            if (tameable.getOwnerId() != null) {
               MMMessageRegistry.getNetwork().sendToNear((ServerLevel)event.getEntity().level(), event.getTarget().getX(), event.getTarget().getY(), event.getTarget().getZ(), 255.0D, new MessageMMTameable(event.getTarget().getUUID().toString(), tameable.getOwnerId().toString(), tameable.getFollowState()));
            }
         }
      }

      @SubscribeEvent
      public static void PlayerInteractEvent(PlayerInteractEvent.EntityInteract event) {
         if (isTameableEntity(event.getTarget()) && event.getHand() == InteractionHand.MAIN_HAND) {
            Mob entity = (Mob)event.getTarget();
            ITameableEntity tameable = EntityUtil.getCapability(event.getTarget(), CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, (Direction)null);
            if (tameable != null) {
               if (tameable.isTamed() && tameable.getOwner(entity) == event.getEntity()) {
                  if (event.getItemStack().isEmpty()) {
                     if (event.getEntity().isShiftKeyDown()) {
                        tameable.setFollowState(tameable.getFollowState() + 1);
                        if (tameable.getFollowState() == 3) {
                           tameable.setFollowState(0);
                        }

                        MMMessageRegistry.getNetwork().sendToNear((ServerLevel)event.getEntity().level(), event.getTarget().getX(), event.getTarget().getY(), event.getTarget().getZ(), 255.0D, new MessageMMTameable(event.getTarget().getUUID().toString(), tameable.getOwnerId().toString(), tameable.getFollowState()));
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

                           MMMessageRegistry.getNetwork().sendToAll(new MessageMMParticle(particleID, 15, (float)entity.getX() + 0.5F, (float)entity.getY() + 0.5F, (float)entity.getZ() + 0.5F, (double)0.0F, (double)0.0F, (double)0.0F, 0));
                        }
                     } else if (entity.canBeLeashed() && !event.getEntity().level().isClientSide) {
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
         MMMessageRegistry.getNetwork().sendToNear((ServerLevel)owner.level(), entity.getX(), entity.getY(), entity.getZ(), 255.0D, new MessageMMTameable(entity.getUUID().toString(), tameable.getOwnerId().toString(), tameable.getFollowState()));
         updateEntityTargetAI(entity);
         entity.goalSelector.addGoal(3, new EntityAITameableFollowOwner(entity, 1.2, 8.0F, 2.0F));
         playHealEffect(entity);
      }

      @SubscribeEvent
      public static void EntityDespawnEvent(MobDespawnEvent event) {
         if (isTameableEntity(event.getEntity())) {
            ITameableEntity tameable = EntityUtil.getCapability(event.getEntity(), CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, (Direction)null);
            if (tameable != null && tameable.isTamed()) {
               event.setResult(MobDespawnEvent.Result.DENY);
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
         return entity != null && entity instanceof Mob && entity.getCapability(CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY) != null;
      }

      protected static void playHealEffect(Entity entity) {
         for(int i = 0; i < 7; ++i) {
            double d0 = entity.level().random.nextGaussian() * 0.02;
            double d1 = entity.level().random.nextGaussian() * 0.02;
            double d2 = entity.level().random.nextGaussian() * 0.02;
            entity.level().addParticle(ParticleTypes.HEART, entity.getX() + (double)(entity.level().random.nextFloat() * entity.getBbWidth() * 2.0F) - (double)entity.getBbWidth(), entity.getY() + (double)0.5F + (double)(entity.level().random.nextFloat() * entity.getBbHeight()), entity.getZ() + (double)(entity.level().random.nextFloat() * entity.getBbWidth() * 2.0F) - (double)entity.getBbWidth(), d0, d1, d2);
         }
      }
   }
}
