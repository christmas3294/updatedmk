package net.kaupenjoe.mccourse.network;

import net.kaupenjoe.mccourse.nbt.PlayerSkillHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateSkillLevelC2SPacket {
    private final int index;
    private final int level;

    public UpdateSkillLevelC2SPacket(int index, int level) {
        this.index = index;
        this.level = level;
    }

    public UpdateSkillLevelC2SPacket(FriendlyByteBuf buf) {
        this.index = buf.readVarInt();
        this.level = buf.readVarInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(index);
        buf.writeVarInt(level);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                PlayerSkillHandler.setSkillLevel(player, index, level);
                PlayerSkillHandler.syncToClient(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}