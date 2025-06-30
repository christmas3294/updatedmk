package net.kaupenjoe.mccourse.battleroyale;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility to store and restore a region of blocks to an NBT file.
 */
public class MapBackup {
    private final ServerLevel level;
    private final BlockPos min;
    private final BlockPos max;
    private final Path file;

    public MapBackup(ServerLevel level, BlockPos pos1, BlockPos pos2, String fileName) {
        this.level = level;
        this.min = new BlockPos(
                Math.min(pos1.getX(), pos2.getX()),
                Math.min(pos1.getY(), pos2.getY()),
                Math.min(pos1.getZ(), pos2.getZ()));
        this.max = new BlockPos(
                Math.max(pos1.getX(), pos2.getX()),
                Math.max(pos1.getY(), pos2.getY()),
                Math.max(pos1.getZ(), pos2.getZ()));
        this.file = level.getServer().getWorldPath(LevelResource.ROOT).resolve(fileName);
    }

    /** Capture all blocks in the region and store them to disk. */
    public void capture() throws IOException {
        CompoundTag root = new CompoundTag();
        ListTag list = new ListTag();
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            CompoundTag tag = new CompoundTag();
            tag.put("Pos", NbtUtils.writeBlockPos(pos));
            tag.put("State", NbtUtils.writeBlockState(level.getBlockState(pos)));
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity != null) {
                tag.put("Entity", entity.saveWithFullMetadata());
            }
            list.add(tag);
        }
        root.put("Blocks", list);
        Files.createDirectories(file.getParent());
        NbtIo.writeCompressed(root, Files.newOutputStream(file));
    }

    /** Replace all blocks in the region with those stored on disk. */
    public void restore() throws IOException {
        if (!Files.exists(file)) {
            return;
        }
        CompoundTag root = NbtIo.readCompressed(Files.newInputStream(file));
        ListTag list = root.getList("Blocks", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag blockTag = (CompoundTag) t;
            BlockPos pos = NbtUtils.readBlockPos(blockTag.getCompound("Pos"));
            BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), blockTag.getCompound("State"));
            level.setBlock(pos, state, 3);
            if (blockTag.contains("Entity")) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be != null) {
                    be.load(blockTag.getCompound("Entity"));
                }
            }
        }
    }

    /** Clears the region by setting everything to air. */
    public void clear() {
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            level.removeBlockEntity(pos);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }
}