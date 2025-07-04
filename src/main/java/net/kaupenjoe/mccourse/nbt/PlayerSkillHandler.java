package net.kaupenjoe.mccourse.nbt;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PlayerSkillHandler {
    public static CompoundTag getSkillData(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        //报错可能是参数问题

      //  String nbttag = player.getUUID() + "skilldata";
        CompoundTag compoundTag = PlayerSkillData.loadFromNBT(persistentData, player);
        for (int i = 1; i < 9; i++) {
            int anInt = compoundTag.getInt(String.valueOf(i));
           // player.sendSystemMessage(Component.nullToEmpty(String.valueOf(anInt)));
            MCCourseMod.hashMap.put(i, anInt);
        }

        //player.sendSystemMessage(Component.nullToEmpty("getSkillData"));
     //   Minecraft.getInstance().player.sendSystemMessage(Component.nullToEmpty(player.toString()));
    //    Minecraft.getInstance().player.sendSystemMessage(Component.nullToEmpty("2"));
       // System.out.println(2);

        //CompoundTag tag = player.getPersistentData();
//     //   Minecraft.getInstance().player.sendSystemMessage(Component.nullToEmpty("3"));
//        return PlayerSkillData.loadFromNBT(tag);
        return compoundTag;
    }

    public static void saveSkillData(ServerPlayer player, PlayerSkillData skillData) {
        CompoundTag tag = player.getPersistentData();
        skillData.saveToNBT(tag);
    }
}