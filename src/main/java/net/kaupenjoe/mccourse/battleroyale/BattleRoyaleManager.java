package net.kaupenjoe.mccourse.battleroyale;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * Simple manager that keeps track of active players in a battle royale match.
 * The implementation is intentionally lightweight and should be extended for a
 * full game mode.
 */
public class BattleRoyaleManager {
    private static final Set<UUID> ACTIVE_PLAYERS = new HashSet<>();
    private static final Map<UUID, InventoryState> SAVED_INVENTORIES = new HashMap<>();
    private static boolean active = false;
    private static long startTick = 0;

    private static final int DAMAGE_GRACE_TICKS = 20 * 10; // 10 seconds
    private static final int ARENA_TIME_TICKS = 20 * 60 * 10; // 10 minutes

    private static final BlockPos START_POS = new BlockPos(88, 132, 328);
    private static final BlockPos ARENA_POS = new BlockPos(147, 8, 287);
    private static final BlockPos OUT_POS = new BlockPos(-237, 18, 334);

    private static final BlockPos BOUNDS_MIN = new BlockPos(-29, 4, 412);
    private static final BlockPos BOUNDS_MAX = new BlockPos(188, 97, 154);

    private static MapBackup MAP_BACKUP;
    private static boolean movedToArena = false;
    private BattleRoyaleManager() {}

    public static boolean isActive() {
        return active;
    }

    /**
     * Starts a match with all currently online players.
     */
    public static void start(MinecraftServer server) {
//        ACTIVE_PLAYERS.clear();
//        SAVED_INVENTORIES.clear();
        startTick = server.overworld().getGameTime();
        movedToArena = false;

        ServerLevel level = server.getLevel(Level.OVERWORLD);


        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            for (UUID activePlayer : ACTIVE_PLAYERS) {
//                player.sendSystemMessage(Component.nullToEmpty("玩家uuid"+player.getUUID().toString()));
//                player.sendSystemMessage(Component.nullToEmpty("玩家uuid1"+activePlayer.toString()));

                if (player.getUUID().toString().equals(activePlayer.toString())) {

                    ACTIVE_PLAYERS.add(player.getUUID());
                    //SAVED_INVENTORIES.put(player.getUUID(), new InventoryState(player));

                    if (level != null) {
                        player.teleportTo(level, START_POS.getX() + 0.5, START_POS.getY(), START_POS.getZ() + 0.5,
                                player.getYRot(), player.getXRot());
                    }

                    player.getInventory().armor.set(2, new ItemStack(Items.ELYTRA));
                    player.sendSystemMessage(Component.literal("Battle Royale started!"));
                }
            }

        }
        active = true;
    }

    /**
     * Stops the current match and clears all state.
     */
    public static void stop(MinecraftServer server) {
        if (!active) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            for (UUID activePlayer : ACTIVE_PLAYERS) {
                if (player.getUUID().equals(activePlayer)) {
                    player.sendSystemMessage(Component.literal("大逃杀游戏结束获得胜利"));
                    restoreInventory(player);
                    teleportOut(player);
                }
            }

        }
        ACTIVE_PLAYERS.clear();
        SAVED_INVENTORIES.clear();
        active = false;
        startTick = 0;
        movedToArena = false;
    }

    public static void addPlayer(ServerPlayer player) {
        //if (active) {
            ACTIVE_PLAYERS.add(player.getUUID());
            SAVED_INVENTORIES.putIfAbsent(player.getUUID(), new InventoryState(player));
            ServerLevel level = player.server.getLevel(Level.OVERWORLD);
//            if (level != null) {
//                player.teleportTo(level, START_POS.getX() + 0.5, START_POS.getY(), START_POS.getZ() + 0.5,
//                        player.getYRot(), player.getXRot());
//            }
            player.getInventory().armor.set(2, new ItemStack(Items.ELYTRA));
            player.sendSystemMessage(Component.literal("Joined the battle."));
      //  }
    }

    public static void removePlayer(ServerPlayer player) {
        ACTIVE_PLAYERS.remove(player.getUUID());
        restoreInventory(player);
        SAVED_INVENTORIES.remove(player.getUUID());
    }
    public static boolean finduuid(UUID id){
        for (UUID activePlayer : ACTIVE_PLAYERS) {
            if (activePlayer.equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Moves the given player to the world spawn of the overworld. Used when
     * players leave the battle so they respawn outside of the arena.
     */
    public static void teleportOut(ServerPlayer player) {
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld != null) {
                    player.teleportTo(overworld,
                            OUT_POS.getX() + 0.5, OUT_POS.getY(), OUT_POS.getZ() + 0.5,
                            player.getYRot(), player.getXRot());
        }
    }

    /**
     * Should be called when a participating player dies.
     */
    public static void handleDeath(ServerPlayer player) {
        if (!active) return;
        restoreInventory(player);
        teleportOut(player);
        ACTIVE_PLAYERS.remove(player.getUUID());
        player.sendSystemMessage(Component.literal("You are out of the battle."));

        MinecraftServer server = player.server;
        if (ACTIVE_PLAYERS.size() <= 1) {
            UUID winnerId = ACTIVE_PLAYERS.iterator().next();
            ServerPlayer winner = server.getPlayerList().getPlayer(winnerId);
            if (winner != null) {
                server.getPlayerList().broadcastSystemMessage(
                        Component.literal(winner.getName().getString() + " wins the battle!"), false);
            }
            stop(server);
        }
    }

    public static Set<UUID> getActivePlayers() {
        return Collections.unmodifiableSet(ACTIVE_PLAYERS);
    }

    private static void restoreInventory(ServerPlayer player) {
        InventoryState state = SAVED_INVENTORIES.get(player.getUUID());
        if (state != null) {
            state.apply(player.getInventory());
        }
    }

    private static class InventoryState {
        private final NonNullList<ItemStack> items;
        private final NonNullList<ItemStack> armor;
        private final NonNullList<ItemStack> offhand;

        InventoryState(ServerPlayer player) {
            Inventory inv = player.getInventory();
            this.items = copy(inv.items);
            this.armor = copy(inv.armor);
            this.offhand = copy(inv.offhand);
            clear(inv);
        }

        void apply(Inventory inv) {
            copyInto(this.items, inv.items);
            copyInto(this.armor, inv.armor);
            copyInto(this.offhand, inv.offhand);
            inv.setChanged();
        }

        private static void clear(Inventory inv) {
            for (int i = 0; i < inv.items.size(); i++) {
                inv.items.set(i, ItemStack.EMPTY);
            }
            for (int i = 0; i < inv.armor.size(); i++) {
                inv.armor.set(i, ItemStack.EMPTY);
            }
            for (int i = 0; i < inv.offhand.size(); i++) {
                inv.offhand.set(i, ItemStack.EMPTY);
            }
            inv.setChanged();
        }

        private static NonNullList<ItemStack> copy(NonNullList<ItemStack> src) {
            NonNullList<ItemStack> list = NonNullList.withSize(src.size(), ItemStack.EMPTY);
            for (int i = 0; i < src.size(); i++) {
                list.set(i, src.get(i).copy());
            }
            return list;
        }

        private static void copyInto(NonNullList<ItemStack> from, NonNullList<ItemStack> to) {
            for (int i = 0; i < from.size() && i < to.size(); i++) {
                to.set(i, from.get(i).copy());
            }
        }
    }
    public static boolean canDealDamage(MinecraftServer server) {
        if (!active) return true;
        long elapsed = server.overworld().getGameTime() - startTick;
        return elapsed >= DAMAGE_GRACE_TICKS;
    }

    public static void tick(ServerLevel level) {
        if (!active) return;
        long elapsed = level.getGameTime() - startTick;
        if (elapsed >= ARENA_TIME_TICKS && !movedToArena) {
            movedToArena = true;
            for (UUID id : ACTIVE_PLAYERS) {
                ServerPlayer p = level.getServer().getPlayerList().getPlayer(id);
                if (p != null) {
                    p.teleportTo(level, ARENA_POS.getX() + 0.5, ARENA_POS.getY(), ARENA_POS.getZ() + 0.5,
                            p.getYRot(), p.getXRot());
                }
            }
        }
    }

    public static void enforceBounds(ServerPlayer player) {
        if (!active) return;
        if (!ACTIVE_PLAYERS.contains(player.getUUID())) return;
        double x = Mth.clamp(player.getX(), BOUNDS_MIN.getX(), BOUNDS_MAX.getX() + 1);
        double y = Mth.clamp(player.getY(), BOUNDS_MIN.getY(), BOUNDS_MAX.getY() + 1);
        double z = Mth.clamp(player.getZ(), BOUNDS_MIN.getZ(), BOUNDS_MAX.getZ() + 1);
        if (x != player.getX() || y != player.getY() || z != player.getZ()) {

        }else{

            player.teleportTo(player.serverLevel(), x, y, z, player.getYRot(), player.getXRot());
        }
    }

}