package net.kaupenjoe.mccourse.nbt;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.network.ModMessages;
import net.kaupenjoe.mccourse.network.OpenTalentScreenS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerSkillHandler {
    //报错可能是参数问题
    private static String key(ServerPlayer player) {
        return player.getUUID() + "skilldata";
    }
    private static final String SKILL_TAG = "mccourse.skilldata";
    /** Load or create the player's skill data tag. */
    public static CompoundTag getSkillData(ServerPlayer player) {
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        CompoundTag data = persisted.getCompound(SKILL_TAG);
        if (data.isEmpty()) {
            CompoundTag init = new CompoundTag();
            for (int i = 1; i <= 4; i++) {
                init.putInt(String.valueOf(i), 1);
            }
            persisted.put(SKILL_TAG, init);
            player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
            data = init;
        }
        return data.copy();
    }
    /** Set a single skill level and persist it. */
    public static void setSkillLevel(ServerPlayer player, int index, int level) {
        if (level > 11){
            player.sendSystemMessage(Component.nullToEmpty("当前技能无法继续提升"));
        }else {
            CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
            CompoundTag data = persisted.getCompound(SKILL_TAG);
            if (data.isEmpty()) {
                data = getSkillData(player);
            }
            data.putInt(String.valueOf(index), level);
            persisted.put(SKILL_TAG, data);
            player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);

        }
    }

    /**
     * Copies the persisted skill data from {@code original} to {@code clone}.
     */
    public static void copyFrom(ServerPlayer original, ServerPlayer clone) {
        CompoundTag originalPersisted =
                original.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        CompoundTag clonePersisted =
                clone.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);

        clonePersisted.put(SKILL_TAG, originalPersisted.getCompound(SKILL_TAG));
        clone.getPersistentData().put(Player.PERSISTED_NBT_TAG, clonePersisted);
    }


    /** Send the player's current skill data back to the client. */
    public static void syncToClient(ServerPlayer player) {
        CompoundTag data = getSkillData(player);
        ModMessages.sendTo(new OpenTalentScreenS2CPacket(data,player), player);
    }
}