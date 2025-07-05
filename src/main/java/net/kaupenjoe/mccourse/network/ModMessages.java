package net.kaupenjoe.mccourse.network;
import net.kaupenjoe.mccourse.MCCourseMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MCCourseMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int id = 0;

    public static void register() {
        INSTANCE.messageBuilder(BattleRoyaleStateSyncS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(BattleRoyaleStateSyncS2CPacket::new)
                .encoder(BattleRoyaleStateSyncS2CPacket::toBytes)
                .consumerMainThread(BattleRoyaleStateSyncS2CPacket::handle)
                .add();

        INSTANCE.messageBuilder(BattleRoyaleDataSyncS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(BattleRoyaleDataSyncS2CPacket::new)
                .encoder(BattleRoyaleDataSyncS2CPacket::toBytes)
                .consumerMainThread(BattleRoyaleDataSyncS2CPacket::handle)
                .add();

        INSTANCE.messageBuilder(OpenTalentScreenS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenTalentScreenS2CPacket::new)
                .encoder(OpenTalentScreenS2CPacket::toBytes)
                .consumerMainThread(OpenTalentScreenS2CPacket::handle)
                .add();

        INSTANCE.messageBuilder(OpenTalentScreenS2CPacketLevel.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenTalentScreenS2CPacketLevel::new)
                .encoder(OpenTalentScreenS2CPacketLevel::toBytes)
                .consumerMainThread(OpenTalentScreenS2CPacketLevel::handle)
                .add();

        INSTANCE.messageBuilder(UpdateSkillLevelC2SPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(UpdateSkillLevelC2SPacket::new)
                .encoder(UpdateSkillLevelC2SPacket::toBytes)
                .consumerMainThread(UpdateSkillLevelC2SPacket::handle)
                .add();

        INSTANCE.messageBuilder(playerserverSyncS2CPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(playerserverSyncS2CPacket::new)
                .encoder(playerserverSyncS2CPacket::toBytes)
                .consumerMainThread(playerserverSyncS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(playerserverSyncS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(playerserverSyncS2CPacket::new)
                .encoder(playerserverSyncS2CPacket::toBytes)
                .consumerMainThread(playerserverSyncS2CPacket::handle)
                .add();



    }
    public static void send(ServerPlayer player){
        // 例如在某个指令/事件/物品use中
     //   INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new OpenTalentScreenPacket());
    }

    public static void sendTo(Object message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToAll(Object message, MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendTo(message, player);
        }
    }
}