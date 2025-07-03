package net.kaupenjoe.mccourse.client;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Holds client-side state for the battle royale mode.
 */
public class BattleRoyaleClientData {
    public static boolean active = false;
    public static final Set<UUID> players = new HashSet<>();
    public static long startTick = 0;
}