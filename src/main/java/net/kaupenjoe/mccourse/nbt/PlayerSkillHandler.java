package net.kaupenjoe.mccourse.nbt;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.network.ModMessages;
import net.kaupenjoe.mccourse.network.OpenTalentScreenS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PlayerSkillHandler {
    //报错可能是参数问题
    private static String key(ServerPlayer player) {
        return player.getUUID() + "skilldata";
    }
    /** Load or create the player's skill data tag. */
    public static CompoundTag getSkillData(ServerPlayer player) {
        CompoundTag persistent = player.getPersistentData();
        CompoundTag data = persistent.getCompound(key(player));
        if (data.isEmpty()) {
            CompoundTag init = new CompoundTag();
            for (int i = 1; i <= 8; i++) {
                init.putInt(String.valueOf(i), 1);
            }
            persistent.put(key(player), init);
            data = init;
        }
        return data.copy();
    }
    /** Set a single skill level and persist it. */
    public static void setSkillLevel(ServerPlayer player, int index, int level) {
        CompoundTag data = getSkillData(player);
        data.putInt(String.valueOf(index), level);
        player.getPersistentData().put(key(player), data);
    }

    /** Send the player's current skill data back to the client. */
    public static void syncToClient(ServerPlayer player) {
        CompoundTag data = getSkillData(player);
        ModMessages.sendTo(new OpenTalentScreenS2CPacket(data,player), player);
    }
}