package net.kaupenjoe.mccourse.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class PlayerSkillHandler {
    public static PlayerSkillData getSkillData(ServerPlayer player) {
        System.out.println(2);
        CompoundTag tag = player.getPersistentData();
        System.out.println(3);
        return PlayerSkillData.loadFromNBT(tag);
    }

    public static void saveSkillData(ServerPlayer player, PlayerSkillData skillData) {
        CompoundTag tag = player.getPersistentData();
        skillData.saveToNBT(tag);
    }
}