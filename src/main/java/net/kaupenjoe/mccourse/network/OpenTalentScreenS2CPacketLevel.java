package net.kaupenjoe.mccourse.network;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.client.TalentScreen;
import net.kaupenjoe.mccourse.event.BattleRoyaleEvents;
import net.kaupenjoe.mccourse.nbt.PlayerSkillData;
import net.kaupenjoe.mccourse.nbt.PlayerSkillHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenTalentScreenS2CPacketLevel {
    public OpenTalentScreenS2CPacketLevel() {}
int newlevel = 1;
    PlayerSkillData playerSkillData;
    TalentScreen.TalentNode node;
    public OpenTalentScreenS2CPacketLevel(PlayerSkillData skillData, TalentScreen.TalentNode node, int newLevel) {

//      switch (newLevel) {
//          case 0:
//          {
//              // no data
//              player.sendSystemMessage(Component.nullToEmpty("服务器通信"));
//              node.setSkilLevel(node.getSkilLevel() + 1);
//              player.sendSystemMessage(Component.nullToEmpty(String.valueOf(node.getSkilLevel())));
//          }
//          case 1:{
////              // no data
////              player.sendSystemMessage(Component.nullToEmpty("查看等级"));
////              MCCourseMod.checklevel = node.getSkilLevel();
//            //  player.sendSystemMessage(Component.nullToEmpty(String.valueOf(node.getSkilLevel())));
//          }
//      }
        this.playerSkillData = skillData;
        this.node = node;
        this.newlevel = newLevel;

    }

    public OpenTalentScreenS2CPacketLevel(FriendlyByteBuf buf) {
        this.playerSkillData = new PlayerSkillData();
        this.newlevel = buf.readVarInt();
        // TODO: consider reading skill data if needed
    }

    public void toBytes(FriendlyByteBuf buf) {
        // no data
        buf.writeVarInt(newlevel);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
//            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandlers::openTalentScreen);
            //TalentScreen.TalentNode
//            int skillLevel = PlayerSkillHandler.getSkillData(BattleRoyaleEvents.playeronline).getSkillLevel(0);
//            PlayerSkillHandler.getSkillData(BattleRoyaleEvents.playeronline).setSkillLevel(0,skillLevel++);

          //  int skillLevel = playerSkillData.getSkillLevel(node.index);
          //  ++skillLevel;
            playerSkillData.setSkillLevel(node.index, newlevel);
        });
        ctx.get().setPacketHandled(true);
    }
}
