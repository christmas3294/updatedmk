package net.kaupenjoe.mccourse.event;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.battleroyale.BattleRoyaleCommand;
import net.kaupenjoe.mccourse.battleroyale.BattleRoyaleManager;
import net.kaupenjoe.mccourse.battleroyale.ChestSnapshot;
import net.kaupenjoe.mccourse.command.RestoreMapCommand;
import net.kaupenjoe.mccourse.nbt.PlayerSkillHandler;
import net.kaupenjoe.mccourse.network.BattleRoyaleStateSyncS2CPacket;
import net.kaupenjoe.mccourse.network.ModMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

//    @SubscribeEvent
//    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
//        if (BattleRoyaleManager.isActive() && event.getEntity() instanceof ServerPlayer player) {
//            if (BattleRoyaleManager.getActivePlayers().contains(player.getUUID())) {
//
////                    BattleRoyaleManager.addPlayer(player);
//
//            } else {
////                BattleRoyaleManager.handleDeath(player);
////                BattleRoyaleManager.teleportOut(player);
////                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
////                        "Battle in progress - you are not participating."));
//            }
//        }
//    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (BattleRoyaleManager.isActive()) {
                if (BattleRoyaleManager.finduuid(player.getUUID())) {
                    BattleRoyaleManager.teleportOut(player);
                    BattleRoyaleManager.handleDeath(player);
                    BattleRoyaleManager.removePlayer(player);

                }
            } else {

                    BattleRoyaleManager.removePlayer(player);

            }
        }
    }
    public static ServerPlayer playeronline;
    // 玩家加入服务器事件
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            playeronline =player;
            if (BattleRoyaleManager.isActive()) {
                if (BattleRoyaleManager.finduuid(player.getUUID())) {
                    BattleRoyaleManager.teleportOut(player);
                    BattleRoyaleManager.handleDeath(player);
                    BattleRoyaleManager.removePlayer(player);
                }
            } else {

                BattleRoyaleManager.removePlayer(player);

            }
//            ModMessages.sendTo(new BattleRoyaleStateSyncS2CPacket(BattleRoyaleManager.isActive(),
//                    BattleRoyaleManager.getActivePlayers().contains(player.getUUID())), player);
        }


    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (BattleRoyaleManager.finduuid(player.getUUID())) {
                BattleRoyaleManager.teleportOut(player);
            }
            if (BattleRoyaleManager.finduuid(player.getUUID())) {

                BattleRoyaleManager.handleDeath(player);
            }

        }

    }


    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (BattleRoyaleManager.isActive()) {
            if (event.phase == TickEvent.Phase.END) {
                BattleRoyaleManager.tick(event.getServer().overworld());
            }
        }

    }
    private static final Set<BlockSnapshot> REMOVED_BLOCKS = new HashSet<>();
    private static final Set<ChestSnapshot.ChestSnapshotnbt> CHEST_SNAPSHOTS = new HashSet<>();
    private static final Set<BlockPos> RECORDED_CHESTS = new HashSet<>();
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (BattleRoyaleManager.isActive()) {
            if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player) {
                BattleRoyaleManager.enforceBounds(player);
            }
            if (event.player instanceof ServerPlayer player){
                if (BattleRoyaleManager.finduuid(player.getUUID())) {
                    // Player player = event.player;
                    Level level = player.level();
                    // record nearby chests within a 4 block radius
                    BlockPos min = player.blockPosition().offset(-4, -4, -4);
                    BlockPos max = player.blockPosition().offset(4, 4, 4);
                    for (BlockPos chestPos : BlockPos.betweenClosed(min, max)) {
                        if (!RECORDED_CHESTS.contains(chestPos)) {
                            BlockEntity be = level.getBlockEntity(chestPos);
                            if (be instanceof ChestBlockEntity chest) {
                                CompoundTag tag = chest.saveWithFullMetadata();
                                CHEST_SNAPSHOTS.add(new ChestSnapshot.ChestSnapshotnbt(chestPos.immutable(), level.getBlockState(chestPos), tag));
                                RECORDED_CHESTS.add(chestPos.immutable());
                            }
                        }
                    }
                }

            }

        }
    }

    /** Restore all removed blocks. Call when the game ends. */
    public static void restoreBlocks(Level level) {
        for (BlockSnapshot snapshot : REMOVED_BLOCKS) {
            level.setBlockAndUpdate(snapshot.getPos(), snapshot.getCurrentBlock());
        }
        REMOVED_BLOCKS.clear();

        for (ChestSnapshot.ChestSnapshotnbt chest : CHEST_SNAPSHOTS) {
            level.setBlockAndUpdate(chest.pos(), chest.state());
            BlockEntity be = level.getBlockEntity(chest.pos());
            if (be instanceof ChestBlockEntity chestEntity) {
                chestEntity.load(chest.nbt());
                chestEntity.setChanged();
            }
        }
        CHEST_SNAPSHOTS.clear();
        RECORDED_CHESTS.clear();
    }

    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        if (event.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer player) {
            boolean finduuid = BattleRoyaleManager.finduuid(player.getUUID());
            if (BattleRoyaleManager.isActive()){
                if (finduuid) {
                    event.setCanceled(true);
                }
            }

        }



    }

    @SubscribeEvent
    public static void playeritemevent(PlayerEvent.ItemCraftedEvent event) {
        Player player = event.getEntity();
        // 获取合成的物品
        ItemStack craftedItem = event.getCrafting();

        player.sendSystemMessage(Component.nullToEmpty(craftedItem.getDisplayName().getString()));

    }


    @SubscribeEvent
    public static void onInventoryChange(PlayerInteractEvent event) {
        event.getEntity().getInventory().items.forEach(item -> {
            event.getEntity().sendSystemMessage(Component.nullToEmpty(item.getDisplayName().getString()));
        });
    }

        @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
//Player player = event.getEntity();
////        ServerPlayer player1 = player.getServer().getPlayerList().getPlayer(player.getGameProfile().getId());
////        CompoundTag skillData = PlayerSkillHandler.getSkillData(player1);
////        int jumolevel = skillData.getInt(String.valueOf(2));
////        player1.addDeltaMovement(player.getDeltaMovement().normalize().multiply(new Vec3(jumolevel,jumolevel,jumolevel)));

            if (event.getEntity() instanceof Player player) {
         
            }
        }

}