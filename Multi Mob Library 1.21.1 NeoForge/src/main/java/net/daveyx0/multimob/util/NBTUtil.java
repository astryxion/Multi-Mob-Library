package net.daveyx0.multimob.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class NBTUtil {
   public static void setBlockPosToNBT(BlockPos pos, String key, CompoundTag compound) {
      compound.putInt(key + "X", pos.getX());
      compound.putInt(key + "Y", pos.getY());
      compound.putInt(key + "Z", pos.getZ());
   }

   public static BlockPos getBlockPosFromNBT(String key, CompoundTag compound) {
      int X = compound.getInt(key + "X");
      int Y = compound.getInt(key + "Y");
      int Z = compound.getInt(key + "Z");
      return new BlockPos(X, Y, Z);
   }

   public static void setBlockStateToNBT(BlockState state, String key, CompoundTag compound) {
      int stateID = Block.getId(state);
      compound.putInt(key + "ID", stateID);
   }

   public static BlockState getBlockStateFromNBT(String key, CompoundTag compound) {
      return Block.stateById(compound.getInt(key + "ID"));
   }
}
