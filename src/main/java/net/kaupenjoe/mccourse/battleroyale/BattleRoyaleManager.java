package net.kaupenjoe.mccourse.battleroyale;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
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

    private BattleRoyaleManager() {}

    public static boolean isActive() {
        return active;
    }

    /**
     * Starts a match with all currently online players.
     */
    public static void start(MinecraftServer server) {
        ACTIVE_PLAYERS.clear();
        SAVED_INVENTORIES.clear();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ACTIVE_PLAYERS.add(player.getUUID());
            SAVED_INVENTORIES.put(player.getUUID(), new InventoryState(player));
            player.sendSystemMessage(Component.literal("Battle Royale started!"));
        }
        active = true;
    }

    /**
     * Stops the current match and clears all state.
     */
    public static void stop(MinecraftServer server) {
        if (!active) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(Component.literal("Battle Royale ended."));
            restoreInventory(player);
        }
        ACTIVE_PLAYERS.clear();
        SAVED_INVENTORIES.clear();
        active = false;
    }

    public static void addPlayer(ServerPlayer player) {
        if (active) {
            ACTIVE_PLAYERS.add(player.getUUID());
            SAVED_INVENTORIES.putIfAbsent(player.getUUID(), new InventoryState(player));
            player.sendSystemMessage(Component.literal("Joined the battle."));
        }
    }

    public static void removePlayer(ServerPlayer player) {
        ACTIVE_PLAYERS.remove(player.getUUID());
        restoreInventory(player);
        SAVED_INVENTORIES.remove(player.getUUID());
    }

    /**
     * Moves the given player to the world spawn of the overworld. Used when
     * players leave the battle so they respawn outside of the arena.
     */
    public static void teleportOut(ServerPlayer player) {
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld != null) {
            BlockPos pos = overworld.getSharedSpawnPos();
            player.teleportTo(overworld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    player.getYRot(), player.getXRot());
        }
    }

    /**
     * Should be called when a participating player dies.
     */
    public static void handleDeath(ServerPlayer player) {
        if (!active) return;
        ACTIVE_PLAYERS.remove(player.getUUID());
        player.sendSystemMessage(Component.literal("You are out of the battle."));

        MinecraftServer server = player.server;
        if (ACTIVE_PLAYERS.size() == 1) {
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
}