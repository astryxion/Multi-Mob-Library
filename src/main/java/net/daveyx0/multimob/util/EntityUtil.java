package net.daveyx0.multimob.util;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.daveyx0.multimob.core.MMEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;

public class EntityUtil {
   public static Predicate<Entity> isNotPlayer() {
      return (p) -> !(p instanceof Player);
   }

   public static String getBlockStateName(BlockState state) {
      ItemStack stack = new ItemStack(state.getBlock());
      return stack.getHoverName().getString();
   }

   public static void removeWhenDisabled(Entity entity) {
      if (MMEntityRegistry.entities.containsKey(entity.getClass()) && !(Boolean)MMEntityRegistry.entities.get(entity.getClass())) {
         entity.discard();
      }

   }

   @Nullable
   public static LivingEntity getLoadedEntityByUUID(UUID uuid, Level world) {
      if (uuid == null || world == null) {
         return null;
      }
      if (world instanceof ServerLevel) {
         Entity entity = ((ServerLevel)world).getEntity(uuid);
         return entity instanceof LivingEntity living ? living : null;
      }

      // Client / non-server: never scan a world-sized AABB (previous code used
      // Double.MIN/MAX and could hitch every owner lookup). Search near players only.
      for (Player player : world.players()) {
         for (Entity entity : world.getEntities(player, player.getBoundingBox().inflate(128.0D), e -> uuid.equals(e.getUUID()))) {
            if (entity instanceof LivingEntity living) {
               return living;
            }
         }
      }
      return null;
   }

   @Nullable
   public static ItemStack getCustomLootItem(Entity entityIn, ResourceLocation resourceLootTable, ItemStack defaultItem) {
      if (resourceLootTable != null && entityIn.level() instanceof ServerLevel) {
         ServerLevel serverLevel = (ServerLevel) entityIn.level();
         LootTable loottable = serverLevel.getServer().getLootData().getLootTable(resourceLootTable);
         LootParams lootparams = new LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.THIS_ENTITY, entityIn)
            .withParameter(LootContextParams.ORIGIN, entityIn.position())
            .create(LootContextParamSets.ENTITY);
         List<ItemStack> items = loottable.getRandomItems(lootparams);
         Iterator<ItemStack> var5 = items.iterator();
         if (var5.hasNext()) {
            ItemStack itemstack = var5.next();
            return itemstack;
         }
      }

      return defaultItem;
   }

   @Nullable
   public static <T> T getCapability(@Nullable Entity entity, Capability<T> capability, @Nullable Direction facing) {
      if (entity != null) {
         return entity.getCapability(capability).orElse(null);
      }
      return null;
   }

   @Nullable
   public static ItemStack[] getCustomLootItems(Entity entityIn, ResourceLocation resourceLootTable, ItemStack defaultItem) {
      ItemStack[] arrayOfItems = null;
      if (resourceLootTable != null && entityIn.level() instanceof ServerLevel) {
         ServerLevel serverLevel = (ServerLevel) entityIn.level();
         LootTable loottable = serverLevel.getServer().getLootData().getLootTable(resourceLootTable);
         LootParams lootparams = new LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.THIS_ENTITY, entityIn)
            .withParameter(LootContextParams.ORIGIN, entityIn.position())
            .create(LootContextParamSets.ENTITY);
         List<ItemStack> listOfItems = loottable.getRandomItems(lootparams);
         arrayOfItems = new ItemStack[listOfItems.size()];
         int i = 0;

         for(ItemStack itemstack : listOfItems) {
            arrayOfItems[i] = itemstack;
            ++i;
         }
      }

      if (arrayOfItems == null) {
         arrayOfItems = new ItemStack[]{defaultItem};
      }

      return arrayOfItems;
   }

   public static boolean isValidMobLightLevel(LivingEntity entity) {
      BlockPos blockpos = new BlockPos(Mth.floor(entity.getX()), Mth.floor(entity.getBoundingBox().minY), Mth.floor(entity.getZ()));
      if (entity.level().getBrightness(LightLayer.SKY, blockpos) > entity.getRandom().nextInt(32)) {
         return false;
      } else {
         int i = entity.level().getMaxLocalRawBrightness(blockpos);
         if (entity.level().isThundering()) {
            int j = entity.level().getSkyDarken();
            entity.level().setSkyFlashTime(10);
            i = entity.level().getMaxLocalRawBrightness(blockpos);
            entity.level().setSkyFlashTime(j);
         }

         return i <= entity.getRandom().nextInt(8);
      }
   }

   public static float distanceToSurface(LivingEntity entityLivingBase, Level world) {
      int i = Mth.floor(entityLivingBase.getX());
      int j = Mth.floor(entityLivingBase.getBoundingBox().minY);
      int k = Mth.floor(entityLivingBase.getZ());
      BlockPos pos = new BlockPos(i, j, k);
      BlockState l = world.getBlockState(pos);
      if (l != null && l.getFluidState().isSource()) {
         for(int j1 = 1; j1 < 64; ++j1) {
            BlockPos pos1 = new BlockPos(i, j + j1, k);
            BlockState i1 = world.getBlockState(pos1);
            if (i1.getBlock() == Blocks.AIR || !i1.getFluidState().isSource()) {
               return (float)j1;
            }
         }
      }

      return -1.0F;
   }

   public static Object[] searchTree(Entity entity, double d) {
      Object[] states = new Object[3];
      AABB axisalignedbb = new AABB(entity.getX() - d, entity.getY() - d, entity.getZ() - d, entity.getX() + d, entity.getY() + d, entity.getZ() + d);
      int n = Mth.floor(axisalignedbb.minX);
      int o = Mth.floor(axisalignedbb.maxX);
      int p = Mth.floor(axisalignedbb.minY);
      int q = Mth.floor(axisalignedbb.maxY);
      int r = Mth.floor(axisalignedbb.minZ);
      int s = Mth.floor(axisalignedbb.maxZ);

      for(int p1 = n; p1 < o; ++p1) {
         for(int q1 = p; q1 < q; ++q1) {
            for(int n2 = r; n2 < s; ++n2) {
               BlockPos pos = new BlockPos(p1, q1, n2);
               BlockState state = entity.level().getBlockState(pos);
               if (state != null && !state.isAir() && state != null && (state.is(net.minecraft.tags.BlockTags.LOGS) || state.getBlock() instanceof RotatedPillarBlock && state.getMapColor(entity.level(), pos) == MapColor.WOOD)) {
                  states[0] = state;

                  for(int l = 0; l < 64; ++l) {
                     BlockPos pos2 = new BlockPos(p1, q1 + l, n2);
                     BlockState state2 = entity.level().getBlockState(pos2);
                     if (state2 != null && !state2.isAir() && state2 != null && state2.is(net.minecraft.tags.BlockTags.LEAVES)) {
                        states[1] = state2;
                        states[2] = pos2;
                        return states;
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   protected void writePos(BlockPos pos, FriendlyByteBuf buf) {
      buf.writeInt(pos.getX());
      buf.writeInt(pos.getY());
      buf.writeInt(pos.getZ());
   }

   protected BlockPos readPos(FriendlyByteBuf buf) {
      int x = buf.readInt();
      int y = buf.readInt();
      int z = buf.readInt();
      return new BlockPos(x, y, z);
   }
}
