package net.kaupenjoe.mccourse.event;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.battleroyale.BattleRoyaleCommand;
import net.kaupenjoe.mccourse.battleroyale.BattleRoyaleManager;
import net.kaupenjoe.mccourse.battleroyale.ChestSnapshot;
import net.kaupenjoe.mccourse.command.RestoreMapCommand;
import net.kaupenjoe.mccourse.csgodemo.Command.BombModeCommand;
import net.kaupenjoe.mccourse.nbt.PlayerSkillHandler;
import net.kaupenjoe.mccourse.network.BattleRoyaleStateSyncS2CPacket;
import net.kaupenjoe.mccourse.network.ModMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
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

import java.util.*;

@Mod.EventBusSubscriber(modid = MCCourseMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BattleRoyaleEvents {
    private static final Set<BlockSnapshot> REMOVED_BLOCKS = new HashSet<>();
    private static final Set<ChestSnapshot.ChestSnapshotnbt> CHEST_SNAPSHOTS = new HashSet<>();
    private static final Set<BlockPos> RECORDED_CHESTS = new HashSet<>();
    private static final Map<UUID, Integer> qdtfcount = new HashMap<>();
    private static final Map<UUID, Integer> CRAFT_COUNTERS = new HashMap<>();

    private static final Map<UUID, Integer> qxjstfcount = new HashMap<>();
    private static final Map<UUID, Integer> rltfcount = new HashMap<>();


    private static final int CRAFTS_PER_LEVEL = 8;
    private static final int KILLS_PER_LEVEL = 8;
    private static final int SKILL_INDEX = 2;
    private static final double SMELT_SUCCESS_CHANCE = 0.2;
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new BattleRoyaleCommand(event.getDispatcher());
        new RestoreMapCommand(event.getDispatcher());
        new BombModeCommand(event.getDispatcher());


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
                BattleRoyaleManager.teleportOut(player,1);
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
                    BattleRoyaleManager.teleportOut(player,1);
                    BattleRoyaleManager.handleDeath(player);
                    BattleRoyaleManager.removePlayer(player);

                }
            } else {

                    BattleRoyaleManager.removePlayer(player);

            }
        }
    }
  //  public static ServerPlayer playeronline;
    // 玩家加入服务器事件
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
        //    playeronline =player;
            if (BattleRoyaleManager.isActive()) {
                if (BattleRoyaleManager.finduuid(player.getUUID())) {
                    BattleRoyaleManager.teleportOut(player,1);
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
                BattleRoyaleManager.teleportOut(player,1);
                BattleRoyaleManager.handleDeath(player);
            }
//            if (BattleRoyaleManager.finduuid(player.getUUID())) {
//
//
//            }

            // 检查被击杀的是否是凋零
            if (event.getEntity() instanceof Creeper creeper) {

                // 检查击杀者是否为玩家
                if (event.getSource().getEntity() instanceof ServerPlayer killer) {
                    CompoundTag data = PlayerSkillHandler.getSkillData(killer);
                    int current = data.getInt(String.valueOf("3"));
                    if (current == 10){}else {
                        qdtfcount.put(killer.getUUID(), qdtfcount.getOrDefault(killer.getUUID(), 0) + 1);
                        if (qdtfcount.get(killer.getUUID()) == 10) {
                            qdtfcount.put(killer.getUUID(), 0);
                            PlayerSkillHandler.setSkillLevel(player, 3, current + 1);
                            PlayerSkillHandler.syncToClient(player);
                        }
                        // 执行击杀凋零后触发的逻辑
                        // 例如：给玩家奖励
                        //击杀凋零提高技能3


                        // 这里可以执行你想要的其他逻辑
                        // 例如给玩家奖励物品、经验等
                    }
                }

            }

        }

        if (event.getSource().getEntity() instanceof ServerPlayer killer && event.getEntity() instanceof ServerPlayer) {
            UUID id = killer.getUUID();
           // int kills = KILL_COUNTERS.getOrDefault(id, 0) + 1;
          //  KILL_COUNTERS.put(id, kills);
            CompoundTag data = PlayerSkillHandler.getSkillData(killer);
            int current = data.getInt(String.valueOf("3"));
            System.out.println("盗贼等级"+current);

            if (new Random().nextInt(10) >current) {
                //正常不掉落装备
            }else {
                killer.sendSystemMessage(Component.nullToEmpty("触发盗贼天赋"));
                //被击杀玩家装备掉落代码
                // 被击杀玩家的装备掉落
                ServerPlayer victim = (ServerPlayer) event.getEntity();
                if (victim != null) {
                    boolean isplayer = false;
                    // 获取被击杀玩家的物品栏
                    for (ItemStack item : victim.getInventory().items) {
                        if (!item.isEmpty()) {
                            if (new Random().nextInt(10) <=2) {
                                isplayer = true;
                                // 创建掉落物品的实体并在世界中生成
//                            victim.level().spawnEntity(
//                                    item.createEntity(victim.world, victim.getBlockPos(), victim.getRotationVec(1.0F))
//                            );
                                // 删除玩家的物品
//                            item.decrement(item.getCount());

                                //     victim.level().explode(item.getEntityRepresentation(),victim.getX())
                                victim.spawnAtLocation(item.copy());
                                item.setCount(0);
                                item.setTag(null);
                                victim.sendSystemMessage(Component.nullToEmpty("装备掉落"+item.getDisplayName()));
                            }

                        }
                        if (isplayer){
                            break;
                        }


                    }



                }
            }
//            if (kills >= KILLS_PER_LEVEL) {
//
//                PlayerSkillHandler.setSkillLevel(killer, SKILL_INDEX, current);
//                PlayerSkillHandler.syncToClient(killer);
//                KILL_COUNTERS.put(id, kills - KILLS_PER_LEVEL);
//            }

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
    //  private static final double SMELT_SUCCESS_CHANCE = 0.2;
//合成物品事件
    @SubscribeEvent
    public static void playeritemevent(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {

            UUID id = player.getUUID();
            int crafted = event.getCrafting().getCount();
            event.getEntity().sendSystemMessage(Component.nullToEmpty(event.getCrafting().getDisplayName().getString()));
            int total = CRAFT_COUNTERS.getOrDefault(id, 0) + 1;
            int i = new Random().nextInt(10);
            CompoundTag skillData = PlayerSkillHandler.getSkillData(player);
            int skilllevel = skillData.getInt("2");
            if (i >= skilllevel){
                player.sendSystemMessage(Component.nullToEmpty("尝试合成失败"));
                event.getCrafting().setCount(0);
            }else {
                if (event.getCrafting().getDisplayName().getString().equals("[Block of Diamond]")) {
                    CRAFT_COUNTERS.put(id, total);
                    if (total >= 8) {
                        CompoundTag data = PlayerSkillHandler.getSkillData(player);
                        int current = data.getInt(String.valueOf("2"));
                        PlayerSkillHandler.setSkillLevel(player, 2, current + 1);
                        PlayerSkillHandler.syncToClient(player);
                        CRAFT_COUNTERS.put(id, 0);
                    }
                }

            }


        }

    }


    @SubscribeEvent
    public static void onItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            int i = new Random().nextInt(10);
            CompoundTag skillData = PlayerSkillHandler.getSkillData(player);
            if (i >= skillData.getInt("1")) {
                ItemStack result = event.getSmelting();
             //   player.getInventory().removeItem(result);
                result.setCount(result.getCount() /2);
               // event.getSmelting()
//                event.setCanceled(true);
//                ItemStack smelting = event.getSmelting();
//                smelting.setCount(smelting.getCount() - 1);

              //  player.sendSystemMessage(Component.nullToEmpty("物品燃烧失败"));

            }else {
                //每次成功燃烧400个物品 升级技能1
                //每次成功合成8个钻石块 升级技能2
                //击杀凋零2次 升级技能3
                //大逃杀获取胜利4次 升级技能4

                if (PlayerSkillHandler.getSkillData(player).getInt("1") == 10) {
                }else {
                    int total = rltfcount.getOrDefault(player.getUUID(), 0) + 1;
                    if (total >= 200){
                        int current = skillData.getInt(String.valueOf("1"));
                        PlayerSkillHandler.setSkillLevel(player, 1, current + 1);
                        PlayerSkillHandler.syncToClient(player);
                        rltfcount.put(player.getUUID(), 0);
                    }
                }



//
//                UUID id = player.getUUID();
//                int smelted = event.getSmelting().getCount();
//                int total = SMELT_COUNTERS.getOrDefault(id, 0) + smelted;
//                SMELT_COUNTERS.put(id, total);
//
//                if (total >= SMELTS_PER_LEVEL) {
//                    CompoundTag data = PlayerSkillHandler.getSkillData(player);
//                    int current = data.getInt(String.valueOf(SKILL_INDEX));
//                    PlayerSkillHandler.setSkillLevel(player, SKILL_INDEX, current + 1);
//                    PlayerSkillHandler.syncToClient(player);
//                    SMELT_COUNTERS.put(id, total - SMELTS_PER_LEVEL);

            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        // 检查是否是玩家受到的伤害
        if (event.getEntity() instanceof ServerPlayer player) {
            // 获取伤害来源
            DamageSource damageSource = event.getSource();

            // 检查攻击者是否是玩家（或者其他生物）
            if (damageSource.getEntity() instanceof ServerPlayer attacker) {
                // 获取造成伤害的物品
                ItemStack weapon = attacker.getMainHandItem(); // 获取玩家主手物品
                if (!weapon.isEmpty()) {
                    // 你可以在这里添加逻辑来检测物品的类型或执行其他操作
//                    System.out.println("攻击者使用了物品：" + weapon.getItem().getDescriptionId());
//                    System.out.println("攻击者使用了物品：" + weapon.getItem().getDescription().getString());
                    if (weapon.getItem().getDescriptionId().contains("item.tacz")) {
                        //tacz武器掉落
                        int i = new Random().nextInt(18);
                        CompoundTag skillData = PlayerSkillHandler.getSkillData(player);
                        if (i <= skillData.getInt("4")) {
                            player.sendSystemMessage(Component.nullToEmpty("触发枪械减伤天赋"));
                            // 获取当前伤害值
                            float damage = event.getAmount();
                            // 设置减少的伤害比例，比如减少50%的伤害
                            float reducedDamage = damage * 0.02f;
                            // 设置新的伤害值
                            event.setAmount(reducedDamage);
                        }
                        // 获取当前伤害值
                        float damage = event.getAmount();
                        // 设置减少的伤害比例，比如减少50%的伤害
                        float reducedDamage = damage * 0.08f;
                        // 设置新的伤害值
                        event.setAmount(reducedDamage);
                    }


                }
            }
        }
    }


//    @SubscribeEvent
//    public static void onInventoryChange(PlayerInteractEvent event) {
//        event.getEntity().getInventory().items.forEach(item -> {
//            event.getEntity().sendSystemMessage(Component.nullToEmpty(item.getDisplayName().getString()));
//        });
//    }

//        @SubscribeEvent
//    public static void onJump(LivingEvent.LivingJumpEvent event) {
////Player player = event.getEntity();
//////        ServerPlayer player1 = player.getServer().getPlayerList().getPlayer(player.getGameProfile().getId());
//////        CompoundTag skillData = PlayerSkillHandler.getSkillData(player1);
//////        int jumolevel = skillData.getInt(String.valueOf(2));
//////        player1.addDeltaMovement(player.getDeltaMovement().normalize().multiply(new Vec3(jumolevel,jumolevel,jumolevel)));
//
////            if(!event.getEntity().level().isClientSide() && event.getEntity() instanceof ServerPlayer serverPlayer) {
////                CompoundTag skillData = PlayerSkillHandler.getSkillData(serverPlayer);
////                int jumolevel = skillData.getInt("2");
////                serverPlayer.addDeltaMovement(serverPlayer.getDeltaMovement().normalize()
////                        .multiply(new Vec3(jumolevel, jumolevel, jumolevel)));
////            }
//        }
//  new UpdateSkillLevelC2SPacket(node.index, getlevel(node.index) + 1));

}