package net.kaupenjoe.mccourse.csgodemo.Manager;

import net.kaupenjoe.mccourse.csgodemo.network.BombStateSyncS2CPacket;
import net.kaupenjoe.mccourse.csgodemo.team.BombTeam;
import net.kaupenjoe.mccourse.network.ModMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

import java.util.*;


/**
 * Very small manager for a bomb mode style game.
 * Players are assigned teams when they join and the bomb status is synced to clients.
 */
public class BombModeManager {
    private static final Map<UUID, BombTeam> PLAYERS = new HashMap<>();
    private static Map<UUID, BombTeam> DeadthPLAYERS = new HashMap<>();
    private static boolean bombPlanted = false;
    private static boolean active = false;

    private static final BlockPos START_POS = new BlockPos(50, 80, 50);
    private static final BlockPos OUT_POS = new BlockPos(0, 80, 0);
    private static int bombTimer = 0;
    private BombModeManager() {
    }

    public static BombTeam assignTeam() {
        long ct = PLAYERS.values().stream().filter(t -> t == BombTeam.COUNTER_TERRORISTS).count();
        long tt = PLAYERS.values().stream().filter(t -> t == BombTeam.TERRORISTS).count();
        return ct <= tt ? BombTeam.COUNTER_TERRORISTS : BombTeam.TERRORISTS;

    }

    public static boolean findgameplayer(UUID uuid) {
        return PLAYERS.containsKey(uuid);
    }

    public static BombTeam getplayerteam(UUID uuid) {
       return PLAYERS.get(uuid);
    }


    //通过BombModeManager.PLAYERS 维护玩家的团队队伍
    public static void addPlayer(ServerPlayer player) {
        BombTeam team = assignTeam();
        PLAYERS.put(player.getUUID(), team);
        player.sendSystemMessage(Component.literal("加入团队 " + team));
        syncAll(player.server);
        teleportToMap(player,team);

    }

    public static void removePlayer(ServerPlayer player) {
        PLAYERS.remove(player.getUUID());
        teleportOut(player);
        syncAll(player.server);
    }

    public static void plantBomb(ServerPlayer player) {
        if (!bombPlanted) {
            bombPlanted = true;
            bombTimer = 20 * 60;
            player.server.getPlayerList().broadcastSystemMessage(
                    Component.literal(player.getName().getString() + " tr团队炸弹已经放置"), false);
            syncAll(player.server);
        }
    }

    public static void defuseBomb(ServerPlayer player) {
        if (bombPlanted) {
            bombPlanted = false;
            bombTimer = 0;
            player.server.getPlayerList().broadcastSystemMessage(
                    Component.literal(player.getName().getString() + " ct阵营获胜"), false);
            syncAll(player.server);
            stop(player.server);
        }

    }

    public static boolean isBombPlanted() {
        return bombPlanted;
    }

    public static boolean isActive() {
        return active;
    }

    public static void start(MinecraftServer server) {
        if (active) return;
        active = true;
        bombPlanted = false;
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            if (findgameplayer(p.getUUID())) {
                syncAll(server);
            }
        }
        server.getPlayerList().broadcastSystemMessage(Component.literal("爆破模式开始"), false);
    }

    public static void stop(MinecraftServer server) {
        if (!active) return;
        active = false;
        bombPlanted = false;
        bombTimer = 0;
      //  for (UUID id : new ArrayList<>(PLAYERS.keySet())) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            boolean findgameplayer = findgameplayer(player.getUUID());
            if (findgameplayer){

                ServerPlayer p = server.getPlayerList().getPlayer(player.getUUID());
                if (p != null) {
                    p.setGameMode(GameType.DEFAULT_MODE);
                    teleportOut(p);
                }
            }

        }


      //  }
        PLAYERS.clear();
        server.getPlayerList().broadcastSystemMessage(Component.literal("Bomb mode stopped."), false);
        syncAll(server);
    }

    public static void tick(MinecraftServer server) {
        if (bombPlanted && bombTimer > 0) {
            bombTimer--;
            if (bombTimer == 0) {
                server.getPlayerList().broadcastSystemMessage(
                        Component.literal("游戏结束tr阵营获胜"), false);
                stop(server);
            }
        }
    }

    public static Map<UUID, BombTeam> getPlayers() {
        return Collections.unmodifiableMap(PLAYERS);
    }

    public static void syncAll(MinecraftServer server) {
        ModMessages.sendToAll(new BombStateSyncS2CPacket(bombPlanted, PLAYERS), server);
    }

    public static void setDeadthPLAYERS(ServerPlayer player) {
        PLAYERS.get(player.getUUID()).setDead(true);
    }


    public static boolean checkdeadplayer(MinecraftServer player) {
        int ct = 0;
        int tr = 0;
        for (UUID uuid : PLAYERS.keySet()) {
         //   System.out.println(uuid);
            BombTeam bombTeam = PLAYERS.get(uuid);
          //  System.out.println(bombTeam.isDead());
            if (bombTeam == BombTeam.COUNTER_TERRORISTS && !bombTeam.isDead()) {
                ct++;
            }
            if (bombTeam == BombTeam.TERRORISTS && !bombTeam.isDead()) {
                tr++;
            }

        }
        if (ct == 0 || tr == 0) {
            if (ct == 0) {
                for (ServerPlayer serverPlayer : player.getPlayerList().getPlayers()) {
                    if (BombModeManager.findgameplayer(serverPlayer.getUUID())) {

                        serverPlayer.sendSystemMessage(Component.literal("游戏结束CT阵营获胜"), false);

                    }
                }
                return true;
            } else {

                for (ServerPlayer serverPlayer : player.getPlayerList().getPlayers()) {
                    if (BombModeManager.findgameplayer(serverPlayer.getUUID())) {

                        serverPlayer.sendSystemMessage(Component.literal("游戏结束T阵营获胜"), false);

                    }
                }
                return true;
            }


               }
        return false;
    }


    public static void syncAllremove(MinecraftServer server) {
        ModMessages.sendToAll(new BombStateSyncS2CPacket(bombPlanted, PLAYERS), server);
    }

    private static void teleportToMap(ServerPlayer player,BombTeam team) {
        ServerLevel level = player.server.getLevel(Level.OVERWORLD);
        if (BombTeam.TERRORISTS ==team) {
            if (level != null) {
                player.teleportTo(level, -821,14,-1660,
                        player.getYRot(), player.getXRot());
            }
        }else {
            player.teleportTo(level, -779,14,-1538,
                    player.getYRot(), player.getXRot());


        }

    }

    private static void teleportOut(ServerPlayer player) {
        ServerLevel level = player.server.getLevel(Level.OVERWORLD);
        if (level != null) {
            player.teleportTo(level, OUT_POS.getX() + 0.5, OUT_POS.getY(), OUT_POS.getZ() + 0.5,
                    player.getYRot(), player.getXRot());
        }
    }
}

