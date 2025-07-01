package net.kaupenjoe.mccourse.battleroyale;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ChestSnapshot {
    /** Stores position, state and NBT for a chest block so it can be restored. */
        public record ChestSnapshotnbt(BlockPos pos, BlockState state, CompoundTag nbt) {}
}
