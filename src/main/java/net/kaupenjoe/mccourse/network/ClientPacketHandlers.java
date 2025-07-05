package net.kaupenjoe.mccourse.network;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.client.TalentScreen;
import net.kaupenjoe.mccourse.nbt.PlayerSkillData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandlers {
    public static DistExecutor.SafeRunnable openTalentScreen(CompoundTag playerSkillData, ServerPlayer player) {
        Minecraft mc = Minecraft.getInstance();
      //  mc.setScreen(new TalentScreen(playerSkillData,player));
        mc.setScreen(new TalentScreen(playerSkillData));
        return null;
    }
}