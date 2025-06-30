package net.kaupenjoe.mccourse.event;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.battleroyale.BattleRoyaleCommand;
import net.kaupenjoe.mccourse.battleroyale.BattleRoyaleManager;
import net.kaupenjoe.mccourse.command.RestoreMapCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MCCourseMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BattleRoyaleEvents {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new BattleRoyaleCommand(event.getDispatcher());
        new RestoreMapCommand(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!BattleRoyaleManager.isActive()) {
            return;
        }
        if (event.isWasDeath() && event.getEntity() instanceof ServerPlayer player) {
            if (BattleRoyaleManager.finduuid(player.getUUID())) {
                BattleRoyaleManager.handleDeath(player);
                BattleRoyaleManager.removePlayer(player);
                BattleRoyaleManager.teleportOut(player);
            }

        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (BattleRoyaleManager.isActive() && event.getEntity() instanceof ServerPlayer player) {
            if (BattleRoyaleManager.getActivePlayers().contains(player.getUUID())) {

//                    BattleRoyaleManager.addPlayer(player);

            } else {
//                BattleRoyaleManager.handleDeath(player);
//                BattleRoyaleManager.teleportOut(player);
//                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
//                        "Battle in progress - you are not participating."));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (BattleRoyaleManager.isActive()) {
                if (BattleRoyaleManager.finduuid(player.getUUID())) {
                    BattleRoyaleManager.handleDeath(player);
                    BattleRoyaleManager.removePlayer(player);
                    BattleRoyaleManager.teleportOut(player);
                }
            } else {

                    BattleRoyaleManager.removePlayer(player);

            }
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (BattleRoyaleManager.finduuid(player.getUUID())) {
                BattleRoyaleManager.handleDeath(player);
            }

        }

    }


    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            BattleRoyaleManager.tick(event.getServer().overworld());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player) {
            BattleRoyaleManager.enforceBounds(player);
        }
    }
}