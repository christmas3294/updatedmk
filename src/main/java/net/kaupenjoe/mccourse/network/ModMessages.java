package net.kaupenjoe.mccourse.network;
import net.kaupenjoe.mccourse.MCCourseMod;
import net.minecraft.resources.ResourceLocation;
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
    }

    public static void sendTo(Object message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}