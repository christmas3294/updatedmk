package net.kaupenjoe.mccourse.network;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BattleRoyaleStateSyncS2CPacket {
    private final boolean active;
    private final boolean participant;

    public BattleRoyaleStateSyncS2CPacket(boolean active, boolean participant) {
        this.active = active;
        this.participant = participant;
    }

    public BattleRoyaleStateSyncS2CPacket(FriendlyByteBuf buf) {
        this.active = buf.readBoolean();
        this.participant = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(active);
        buf.writeBoolean(participant);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                if (active) {
                    String msg = participant ? "Battle Royale started!" : "Spectating battle.";
                   // Minecraft.getInstance().player.displayClientMessage(Component.literal(msg), false);
                } else {
                   // Minecraft.getInstance().player.displayClientMessage(Component.literal("Battle Royale ended."), false);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}