package net.kaupenjoe.mccourse.nbt;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.event.BattleRoyaleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class PlayerSkillData {
    private static final String NBT_KEY = BattleRoyaleEvents.playeronline.getName().getString()+"nbt"; // 你用来存储技能数据的键

    // 用Map存储每个技能的等级
    private final Map<Integer, Integer> skillLevels = new HashMap<>();

    public void setSkillLevel(int skillIndex, int level) {
        skillLevels.put(skillIndex, level);
    }

    public int getSkillLevel(int skillIndex) {
      //  Minecraft.getInstance().player.sendSystemMessage(Component.nullToEmpty("6"));
        //   for (int i = 1; i < 9; i++) {
        //                System.out.println("执行初始化");
        //                skillData.setSkillLevel(i,1);
        //            }
//        Integer orDefault = skillLevels.getOrDefault(skillIndex, 1);
//        this.setSkillLevel(skillIndex,orDefault);

        return 1;
    }

    public static void saveToNBT(CompoundTag tag) {
// 初始化新玩家的技能数据
        CompoundTag inittaglevel = new CompoundTag();
        for (int i = 1; i <9; i++) {
            Integer i1 = MCCourseMod.hashMap.get(i);
            inittaglevel.putInt(String.valueOf(i), i1);
        }
        //保存更新数据 需要更新数据时通过更改MCCourseMod.hashMap 再通过savetonbt更新
       tag.put(MCCourseMod.hashname,inittaglevel);
      //  tag.put()
      //  tag.put(NBT_KEY, skillsTag);
    }

    public static CompoundTag loadFromNBT(CompoundTag tag, ServerPlayer playerServer) {
        PlayerSkillData skillData = new PlayerSkillData();
      //  Minecraft.getInstance().player.sendSystemMessage(Component.nullToEmpty("4"));
      //  System.out.println(4);
       // playerServer.sendSystemMessage(Component.nullToEmpty(String.valueOf(tag)));
      //  tag.get(playerServer.getUUID()+"skilldata")
        if (tag.get(playerServer.getUUID()+"skilldata") !=null) {
            playerServer.sendSystemMessage(Component.nullToEmpty("使用当前数据nbt"));
        //    System.out.println(5);
//            CompoundTag skillsTag = tag.getCompound(NBT_KEY);
//            for (String key : skillsTag.getAllKeys()) {
//                skillData.setSkillLevel(Integer.parseInt(key), skillsTag.getInt(key));
//            }
           tag.getAllKeys().forEach(key -> {
               CompoundTag skillTag = tag.getCompound(key);
               for (int i = 1; i < 9; i++) {
                   skillTag.getInt(String.valueOf(i));
               }
           });
        }else {
            playerServer.sendSystemMessage(Component.nullToEmpty("初始化nbt"));
            // 初始化新玩家的技能数据
            CompoundTag inittaglevel = new CompoundTag();

            for (int i = 1; i < 9; i++) {
                inittaglevel.putInt(String.valueOf(i), 1);
            }
            tag.put(playerServer.getUUID()+"skilldata",inittaglevel);

        }
        CompoundTag getLevellist = tag.getCompound(playerServer.getUUID() + "skilldata");
        //playerServer.getPersistentData().


        return getLevellist;
    }
}
