package net.kaupenjoe.mccourse.event;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.nbt.PlayerSkillHandler;
import net.kaupenjoe.mccourse.talent.PlayerTalentData;
import net.kaupenjoe.mccourse.talent.Talent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles applying talent attribute modifiers to players.
 */
@Mod.EventBusSubscriber(modid = MCCourseMod.MOD_ID)
public class TalentEvents {

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        PlayerTalentData.copyFrom(event.getOriginal(), event.getEntity());
        if (event.getOriginal() instanceof ServerPlayer original && event.getEntity() instanceof ServerPlayer clone) {
            PlayerSkillHandler.copyFrom(original, clone);
        }
    }

//    @SubscribeEvent
//    public static void onJump(PlayerInteractEvent.LeftClickEmpty event) {
////      if (event.getEntity() != null){
////
////      }
//        Player player = event.getEntity();
//        ServerPlayer player1 = player.getServer().getPlayerList().getPlayer(player.getGameProfile().getId());
//        CompoundTag skillData = PlayerSkillHandler.getSkillData(player1);
//        int jumolevel = skillData.getInt(String.valueOf(2));
//        player1.addDeltaMovement(player.getDeltaMovement().normalize().multiply(new Vec3(jumolevel,jumolevel,jumolevel)));
//    }
//        Player player = event.getEntity();
//
//        for (Talent talent : Talent.values()) {
//                int count = PlayerTalentData.getTalentCount(player, talent);
//                if (talent.getName().equals("技能A")) {
//                    if (count >0){
//
//                        talent.setCount(count);
//                        player.sendSystemMessage(Component.nullToEmpty(String.valueOf(count)));
//                     //   Vec3 multiply = player.getDeltaMovement().normalize().multiply(count, count, count);
//                    }
//
//                }
//            }
//
//    }

    /**
     * Applies the talent effect identified by {@code name} to the given player.
     */
    public static void applyTalents(Player player, String name) {
        player.sendSystemMessage(Component.nullToEmpty(name));

        for (Talent talent : Talent.values()) {
            if (name.equals(talent.getName())) {
                switch (name) {
                    case "技能A":
                        for (ItemStack item : player.getInventory().items) {
                            if (item.getDisplayName().getString().equals("[钻石块]") && item.getCount() >= 64) {
                                item.setCount(item.getCount() % 64);
                                player.sendSystemMessage(Component.nullToEmpty(item.getDisplayName().getString()));
                                int current = PlayerTalentData.getTalentCount(player, talent);
                                PlayerTalentData.setTalentCount(player, talent, current + 1);
                            }
                        }
                        break;
                    case "技能B":
                        // Additional skill handling can be implemented here.
                        break;
                    default:
                        break;
                }
            }
        }
    }
}