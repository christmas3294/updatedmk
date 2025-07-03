package net.kaupenjoe.mccourse.client;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.context.CommandContext;
import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.battleroyale.BattleRoyaleCommand;
import net.kaupenjoe.mccourse.event.BattleRoyaleEvents;
import net.kaupenjoe.mccourse.nbt.PlayerSkillData;
import net.kaupenjoe.mccourse.nbt.PlayerSkillHandler;
import net.kaupenjoe.mccourse.network.ModMessages;
import net.kaupenjoe.mccourse.network.OpenTalentScreenS2CPacketLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Simple talent GUI screen example.
 */

public class TalentScreen extends Screen {
    private static final ResourceLocation BG_TEXTURE =
            new ResourceLocation(MCCourseMod.MOD_ID, "textures/gui/talent_gui.png");

    /**
     * Data for a single talent node.
     */
    public static class TalentNode {
        public final int x, y;
        public final ResourceLocation icon;
        public boolean unlocked;
        //添加属性
        //技能名称
        public String Skilname;
        //当前技能等级
        public int SkilLevel;
        public int index;

        public TalentNode(int x, int y, ResourceLocation icon, String Skilname, int SkilLevel, int index) {
            this.x = x;
            this.y = y;
            this.icon = icon;
            this.unlocked = false;
            this.index = index;
        }

        public void setSkilLevel(int skilLevel) {
            SkilLevel = skilLevel;
        }

        public int getSkilLevel() {
            return SkilLevel;
        }
    }

    private final List<TalentNode> nodes = new ArrayList<>();

    private int x;
    private int y;
    private final int imageWidth = 256;
    private final int imageHeight = 166;
// 用Map存储每个技能的等级
    private final Map<Integer, Integer> skillLevels = new HashMap<>();

    public void setSkillLevel(int skillIndex, int level) {
        skillLevels.put(skillIndex, level);
    }

    public int getSkillLevel(int skillIndex) {
        return skillLevels.getOrDefault(skillIndex, 0);
    }
    public TalentScreen() {

        super(Component.literal("\u5929\u8d4b\u7cfb\u7edf"));
     //   MinecraftServer server = context.getSource().getServer();
//        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
//            player.sendSystemMessage(Component.nullToEmpty("TalentScreen"));
//        }
//getSkillData
        System.out.println(1);
        nodes.add(new TalentNode(60, 40,
                new ResourceLocation(MCCourseMod.MOD_ID, "textures/gui/rpg_icons.png"),"挖掘",(int) PlayerSkillHandler.getSkillData(BattleRoyaleEvents.playeronline).getSkillLevel(1),1));
        nodes.add(new TalentNode(100, 40,
                new ResourceLocation(MCCourseMod.MOD_ID, "textures/gui/rpg_icons.png"),"喜欢挖掘",PlayerSkillHandler.getSkillData(BattleRoyaleEvents.playeronline).getSkillLevel(2),2));
//        nodes.add(new TalentNode(140, 40,
//                new ResourceLocation(MCCourseMod.MOD_ID, "textures/gui/rpg_icons.png"),"这下挖掘了",1,3));
//        nodes.add(new TalentNode(180, 40,
//                new ResourceLocation(MCCourseMod.MOD_ID, "textures/gui/rpg_icons.png"),"这下挖掘了",2,4));
//        nodes.add(new TalentNode(60, 70,
//                new ResourceLocation(MCCourseMod.MOD_ID, "textures/gui/rpg_icons.png"),"这下挖掘了",2,5));
//        nodes.add(new TalentNode(100, 70,
//                new ResourceLocation(MCCourseMod.MOD_ID, "textures/gui/rpg_icons.png"),"这下挖掘了",2,6));
//        nodes.add(new TalentNode(140, 70,
//                new ResourceLocation(MCCourseMod.MOD_ID, "textures/gui/rpg_icons.png"),"这下挖掘了",3,7));
//        nodes.add(new TalentNode(180, 70,
//                new ResourceLocation(MCCourseMod.MOD_ID, "textures/gui/rpg_icons.png"),"这下挖掘了",4,8));

    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - imageWidth) / 2;
        this.y = (this.height - imageHeight) / 2;
    }
int index =0;
    int index1 = 0;
    int index2 = 0;
    int index3 = 0;
    int index4 = 0;
    int index5 = 0;
    int bczcount = 44;
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        //renderBackground(guiGraphics);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
      //  guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
//int i = 0;
        for (TalentNode node : nodes) {
            boolean hovered = isMouseOverNode(mouseX, mouseY, node);
            int size = hovered ? 16 : 32;
            int drawX = x + node.x - size / 2;
            int drawY = y + node.y - size / 2;

          //补偿值64 8张图片
            //20 * 8 = 160 * 160
            //补偿倍数值3.2
            //512/3.2
            //第二行
            //51.2/3.2
            //
            guiGraphics.blit(node.icon, node.x,node.y, (int) getx(PlayerSkillHandler.getSkillData(BattleRoyaleEvents.playeronline).getSkillLevel(node.index)), (int) gety(node.index), 20, 20);
           // guiGraphics.blit(node.icon, node.x,node.y, (int) getx(node.SkilLevel), (int) gety(node.index)*i, 20,20);
            if (hovered) {
//                guiGraphics.drawCenteredString(this.font, "技能等级"+PlayerSkillHandler.getSkillData(BattleRoyaleEvents.playeronline).getSkillLevel(node.index),
//                        node.x, node.y, 0xFFFF00);
            }else {

//                guiGraphics.drawCenteredString(this.font, "Lv:"+PlayerSkillHandler.getSkillData(BattleRoyaleEvents.playeronline).getSkillLevel(node.index),
//                        node.x, node.y, 0xFFFF00);
            }
//            if (node.unlocked) {
//                guiGraphics.drawCenteredString(this.font, "\u221a",
//                         node.x,  node.y, 0x00FF00);
//            }
          //  i++;
        }
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
    public int gety(int index){
        if (index ==1) {
            return 0;
        }
        return 24 * index - 24;
    }
    public double getx(int dj){
        if (dj == 1){
            return 0;
        }
        return 30 * dj;
    }

    private boolean isMouseOverNode(int mouseX, int mouseY, TalentNode node) {
        int centerX = node.x;
        int centerY = node.y;
        int radius = 12;
        return Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2) <= radius * radius;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (TalentNode node : nodes) {
            if (isMouseOverNode((int) mouseX, (int) mouseY, node)) {
switch (node.index) {
    case 1:
    {
        if (BattleRoyaleEvents.playeronline !=null) {
            ServerPlayer player = BattleRoyaleEvents.playeronline;
        //    ModMessages.sendTo(new OpenTalentScreenS2CPacketLevel(player,node,0),player);
        }

    };
//        case 2:{
//            if (BattleRoyaleEvents.playeronline !=null) {
//                ServerPlayer player = BattleRoyaleEvents.playeronline;
//                ModMessages.sendTo(new OpenTalentScreenS2CPacketLevel(player,node,0),player);
//            }
    case 2:{
        // 升级技能并保存
        if (BattleRoyaleEvents.playeronline != null) {
            ServerPlayer player = BattleRoyaleEvents.playeronline;
            PlayerSkillData skillData = PlayerSkillHandler.getSkillData(player);

            // 更新技能等级
            int currentLevel = skillData.getSkillLevel(node.index);
            int newLevel = currentLevel + 1; // 假设每次点击就升一级
            skillData.setSkillLevel(node.index, newLevel);

            // 保存更新后的技能等级
            PlayerSkillHandler.saveSkillData(player, skillData);

            // 发送更新后的数据到客户端
            ModMessages.sendTo(new OpenTalentScreenS2CPacketLevel(skillData, node, newLevel), player);
            player.sendSystemMessage(Component.nullToEmpty(String.valueOf(skillData.getSkillLevel(2))));
        }



        }
}
                //nodes
                //实现服务器数据同步
                //玩家点击需要查看玩家的金币 2000金币升级一次
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}