package net.kaupenjoe.mccourse.battleroyale;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.nbt.PlayerSkillData;
import net.kaupenjoe.mccourse.nbt.PlayerSkillHandler;
import net.kaupenjoe.mccourse.network.ModMessages;
import net.kaupenjoe.mccourse.network.OpenTalentScreenS2CPacket;
import net.kaupenjoe.mccourse.network.OpenTalentScreenS2CPacketLevel;
import net.kaupenjoe.mccourse.network.playerserverSyncS2CPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;

/**
 * Basic command for managing a simple battle royale match.
 */
public class BattleRoyaleCommand {
    public BattleRoyaleCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("battle")
                .then(Commands.literal("start").requires(cs -> cs.hasPermission(2))
                        .executes(this::start))
                .then(Commands.literal("stop").requires(cs -> cs.hasPermission(2))
                        .executes(this::stop))
                .then(Commands.literal("join")
                        .executes(this::join))
                .then(Commands.literal("opengui")
                        .executes(this::openguilevel))
        );
    }

    private int start(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        if (BattleRoyaleCommand.ismap !=null) {
            if (BattleRoyaleManager.isActive()) {
                context.getSource().sendFailure(Component.literal("Battle already running."));
                return 0;
            }   //  context.getSource().sendSuccess(() -> Component.literal("房间开始"), true);

            if (BattleRoyaleManager.getroomplayercount()) {

                context.getSource().sendSuccess(() -> Component.literal("房间开始游戏"), true);
                //Component.literal(dimension.location().getPath()
                BattleRoyaleManager.start(server,context);




            }else {
                context.getSource().getPlayer().sendSystemMessage(Component.literal("房间人数小于两人"), true);
            }
        }


        return 1;
    }

    private int stop(CommandContext<CommandSourceStack> context) {
        if (BattleRoyaleManager.getroom()) {
            MinecraftServer server = context.getSource().getServer();
            if (!BattleRoyaleManager.isActive()) {
                context.getSource().sendFailure(Component.literal("No active battle."));
                return 0;
            }
            BattleRoyaleManager.stop(server);

            context.getSource().sendSuccess(() -> Component.literal("Battle stopped."), true);
            return 1;  }

        return 0;
    }
    @OnlyIn(Dist.CLIENT)
    public boolean opengui(CommandContext<CommandSourceStack> context){
        ModMessages.send(context.getSource().getPlayer());
        return true;
    }

public static ServerLevel ismap = null;
public static HashMap<String,ServerPlayer> playerserver = new HashMap<>();
    private int join(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
       // ModMessages.sendTo(new playerserverSyncS2CPacket(player,playerserver),player);
      //  CompoundTag skillData = PlayerSkillHandler.getSkillData(player);
      //  MCCourseMod.tag = skillData;
       // playerserver.put(player.getDisplayName().getString(), player);
    //    player.sendSystemMessage(Component.literal(player.getDisplayName().getString()));
       // ModMessages.sendTo(new OpenTalentScreenS2CPacket(skillData,player), player);
        playerserver.put(player.getDisplayName().getString(),player);
        // 例如在某个指令/事件/物品use中

        //  Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new TalentScreen()));
//        if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.isClientSide()) {
//            Minecraft.getInstance().setScreen(new TalentScreen());
//        }
//        if (FMLCommonSetupEvent.getDist() == Dist.CLIENT) {
//            Minecraft.getInstance().setScreen(new TalentScreen());
//        }

        //   Player player1 = context.getSource().getPlayer();
     //   Minecraft.getInstance().setScreen(new TalentScreen());
      //  ServerPlayer player = context.getSource().getPlayer();
        ResourceKey<Level> dimension = player.level().dimension();
   //     if (player.level().dimension().equals("gangame")) {

            ismap = player.level().getServer().getLevel(dimension);
      //  context.getSource().getPlayer().sendSystemMessage(Component.nullToEmpty(ismap.toString()));
      //  context.getSource().sendSuccess(() -> Component.literal(String.valueOf(ismap)), false);
        if (ismap.toString().contains("gangame")) {
          //  context.getSource().sendSuccess(() -> Component.literal("加入房间"), false);
            //        player.sendSystemMessage(Component.literal(String.valueOf(dimension)), true);
//        player.sendSystemMessage(Component.literal(String.valueOf(ismap)), true);
            //player.sendSystemMessage(Component.literal(dimension.location().getPath()));
            if (!BattleRoyaleManager.isActive()) {
                if (!BattleRoyaleManager.finduuid(context.getSource().getPlayer().getUUID())) {
                    BattleRoyaleManager.teleportOut(context.getSource().getPlayer(),2);
                    // context.getSource().sendFailure(Component.literal("No active battle."));
                    //return 0;
                    BattleRoyaleManager.addPlayer(player);
                    context.getSource().sendSuccess(() -> Component.literal("加入房间"), false);
                }else {
                    context.getSource().sendSuccess(() -> Component.literal("已经加入房间"), false);
                }

            }
            //  }
        }
       else{
            player.sendSystemMessage(Component.literal("你需要去到枪械地图世界"));
        }

        return 1;
    }

    private int openguilevel(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        ModMessages.sendTo(new playerserverSyncS2CPacket(player,playerserver),player);
        CompoundTag skillData = PlayerSkillHandler.getSkillData(player);
        //  MCCourseMod.tag = skillData;
        // playerserver.put(player.getDisplayName().getString(), player);
        player.sendSystemMessage(Component.literal(player.getDisplayName().getString()));
        ModMessages.sendTo(new OpenTalentScreenS2CPacket(skillData,player), player);
       // playerserver.put(player.getDisplayName().getString(),player);
        return 1;

    }
    public static void updateSkillLevel(ServerPlayer player, int skillIndex, int newLevel) {
//        PlayerSkillData skillData = PlayerSkillHandler.getSkillData(player);
//        skillData.setSkillLevel(skillIndex, newLevel);
//        PlayerSkillHandler.saveSkillData(player, skillData);

        // 向客户端发送更新后的技能数据
      //  ModMessages.sendTo(new OpenTalentScreenS2CPacketLevel(skillData, skillIndex, newLevel), player);
    }
}