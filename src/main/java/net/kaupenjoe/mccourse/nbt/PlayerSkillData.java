package net.kaupenjoe.mccourse.nbt;

import net.kaupenjoe.mccourse.event.BattleRoyaleEvents;
import net.minecraft.nbt.CompoundTag;

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
        System.out.println(7);
        //   for (int i = 1; i < 9; i++) {
        //                System.out.println("执行初始化");
        //                skillData.setSkillLevel(i,1);
        //            }
        Integer orDefault = skillLevels.getOrDefault(skillIndex, 1);
        this.setSkillLevel(skillIndex,orDefault);

        return 1;
    }

    public void saveToNBT(CompoundTag tag) {
        CompoundTag skillsTag = new CompoundTag();
        for (Map.Entry<Integer, Integer> entry : skillLevels.entrySet()) {
            skillsTag.putInt(String.valueOf(entry.getKey()), entry.getValue());
        }
        tag.put(NBT_KEY, skillsTag);
    }

    public static PlayerSkillData loadFromNBT(CompoundTag tag) {
        PlayerSkillData skillData = new PlayerSkillData();
        System.out.println(4);
        if (tag.contains(NBT_KEY)) {
            System.out.println(5);
            CompoundTag skillsTag = tag.getCompound(NBT_KEY);
            for (String key : skillsTag.getAllKeys()) {
                skillData.setSkillLevel(Integer.parseInt(key), skillsTag.getInt(key));
            }
        }else {
         //   for (int i = 1; i < 9; i++) {
                System.out.println(6);

          //  }
        }
        return skillData;
    }
}
