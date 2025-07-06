package net.kaupenjoe.mccourse.csgodemo.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.csgodemo.Manager.BombModeManager;
import net.kaupenjoe.mccourse.csgodemo.team.BombTeam;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge event hooks for the bomb mode demonstration.
 */
@Mod.EventBusSubscriber(modid = MCCourseMod.MOD_ID)
public class BombModeCommand {
    public BombModeCommand(CommandDispatcher< CommandSourceStack > dispatcher){
         //   public BattleRoyaleCommand(CommandDispatcher< CommandSourceStack > dispatcher) {
         //   dispatcher.register(Commands.literal("battle")
//                    .then(Commands.literal("start").requires(cs -> cs.hasPermission(2))
//                            .executes(this::start))
//                    .then(Commands.literal("stop").requires(cs -> cs.hasPermission(2))
//                            .executes(this::stop))
//                    .then(Commands.literal("join")
//                            .executes(this::join))
//                    .then(Commands.literal("opengui")
//                            .executes(this::openguilevel))
//            );
     //   }

        dispatcher.register(Commands.literal("battlecsgo")   .then(Commands.literal("start").requires(cs -> cs.hasPermission(2))
                .executes(this::start))
                    .then(Commands.literal("join").requires(cs -> cs.hasPermission(2))
                            .executes(this::join)));
               //     .then(Commands.literal("stop").requires(cs -> cs.hasPermission(2))

    }
    public int start(CommandContext<CommandSourceStack> context){
        ServerPlayer player = context.getSource().getPlayer();
        if (!BombModeManager.isActive()) {
            BombModeManager.start(context.getSource().getServer());
        }
        return 1;
    }
    public int join(CommandContext<CommandSourceStack> context){
        ServerPlayer player = context.getSource().getPlayer();
        if (!BombModeManager.isActive()) {
            BombModeManager.addPlayer(player);
        }
        return 1;
    }
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
//        if (event.getEntity() instanceof ServerPlayer player) {
//            if (BombModeManager.isActive()) {
//                BombModeManager.addPlayer(player);
//            }
//        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (BombModeManager.getPlayers().containsKey(player.getUUID())) {
                BombModeManager.removePlayer(player);
            }
        }
    }
    @SubscribeEvent
    public static void death(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
           if (BombModeManager.findgameplayer(player.getUUID())){
//               PLAYERS.remove(player.getUUID());

               BombModeManager.syncAll(player.server);
               player.setGameMode(GameType.SPECTATOR);
               BombModeManager.setDeadthPLAYERS(player);
               if (BombModeManager.checkdeadplayer(player.server)) {
                   BombModeManager.stop(player.server);
               }

           }
        }
    }


        @SubscribeEvent
        public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                //游戏是否开始 true继续执行
                if (!BombModeManager.isActive()) {
                    return;
                }
                // 获取玩家当前位置
                double playerX = player.getX();
                double playerY = player.getY();
                double playerZ = player.getZ();

                // 设置炸弹放置/拆除的有效区域
                double minX = -834, maxX = -824;
                double minY = 12, maxY = 16;
                double minZ = -1565, maxZ = -1556;

                // 检查玩家是否在区域内
                boolean isInArea = playerX >= minX && playerX <= maxX &&
                        playerY >= minY && playerY <= maxY &&
                        playerZ >= minZ && playerZ <= maxZ;

                if (!isInArea) {
                    return;  // 玩家不在区域内，不执行后续操作
                }

                if (BombModeManager.findgameplayer(player.getUUID())) {
                    if (BombModeManager.getplayerteam(player.getUUID()) == BombTeam.TERRORISTS) {
                        if (event.getItemStack().is(Items.TNT)) {
                            BombModeManager.plantBomb(player);
                            event.setCanceled(true);
                        }
                    }else {
                          if (event.getItemStack().is(Items.SHEARS)) {
                            BombModeManager.defuseBomb(player);
                            event.setCanceled(true);
                        }
                    }


                }
            }
        }
        @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (BombModeManager.isActive() && event.phase == TickEvent.Phase.END) {
            BombModeManager.tick(event.getServer());
        }
    }
}