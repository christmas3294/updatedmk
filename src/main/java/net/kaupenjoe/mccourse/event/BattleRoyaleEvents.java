package net.kaupenjoe.mccourse.event;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.battleroyale.BattleRoyaleCommand;
import net.kaupenjoe.mccourse.battleroyale.BattleRoyaleManager;
import net.kaupenjoe.mccourse.battleroyale.ChestSnapshot;
import net.kaupenjoe.mccourse.command.RestoreMapCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

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

    // 玩家加入服务器事件
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
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
}