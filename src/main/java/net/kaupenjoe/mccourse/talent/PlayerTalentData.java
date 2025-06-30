package net.kaupenjoe.mccourse.talent;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Helper class to store and retrieve player talent information.
 */
public class PlayerTalentData {
    private static final String POINTS_KEY = "mccourse.talent_points";
    private static final String TALENT_PREFIX = "mccourse.talent_";

    public static int getPoints(Player player) {
        return player.getPersistentData().getInt(POINTS_KEY);
    }

    public static void addPoints(Player player, int amount) {
        int current = getPoints(player);
        player.getPersistentData().putInt(POINTS_KEY, current + amount);
    }

    public static boolean hasTalent(Player player, Talent talent) {
        return player.getPersistentData().getBoolean(TALENT_PREFIX + talent.name().toLowerCase());
    }

    public static boolean unlockTalent(Player player, Talent talent) {
        int points = getPoints(player);
        if (points <= 0 || hasTalent(player, talent)) {
            return false;
        }
        player.getPersistentData().putBoolean(TALENT_PREFIX + talent.name().toLowerCase(), true);
        player.getPersistentData().putInt(POINTS_KEY, points - 1);
        return true;
    }


    private static final String TAG_PREFIX = "mccourse.talent.";



    /**
     * Copies persisted talent values from {@code original} to {@code clone}.
     * The values are stored inside {@link Player#PERSISTED_NBT_TAG} to survive
     * player deaths.
     */
    public static void copyFrom(Player original, Player clone) {
        CompoundTag originalPersisted =
                original.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        CompoundTag clonePersisted =
                clone.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);

        for (Talent talent : Talent.values()) {

            String key = TAG_PREFIX + talent.getName();
            clonePersisted.putInt(key,    getTalentCount(original, talent));

        }

        clone.getPersistentData().put(Player.PERSISTED_NBT_TAG, clonePersisted);
    }

    public static int getTalentCount(Player player, Talent talent) {
        CompoundTag persisted =
                player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        return persisted.getInt(TAG_PREFIX + talent.getName());
    }

    public static void setTalentCount(Player player, Talent talent, int value) {
        CompoundTag persisted =
                player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        persisted.putInt(TAG_PREFIX + talent.getName(), value);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
    }


}