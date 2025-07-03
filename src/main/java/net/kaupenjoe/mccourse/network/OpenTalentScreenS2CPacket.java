package net.kaupenjoe.mccourse.network;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenTalentScreenS2CPacket {
    public OpenTalentScreenS2CPacket() {}

    public OpenTalentScreenS2CPacket(FriendlyByteBuf buf) {
        // no data
    }

    public void toBytes(FriendlyByteBuf buf) {
        // no data
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandlers::openTalentScreen);
        });
        ctx.get().setPacketHandled(true);
    }
}