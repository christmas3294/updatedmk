package net.kaupenjoe.mccourse.network;


import net.kaupenjoe.mccourse.client.BattleRoyaleClientData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Sends the complete battle royale state to clients.
 */
public class BattleRoyaleDataSyncS2CPacket {
    private final boolean active;
    private final Set<UUID> players;
    private final long startTick;

    public BattleRoyaleDataSyncS2CPacket(boolean active, Set<UUID> players, long startTick) {
        this.active = active;
        this.players = new HashSet<>(players);
        this.startTick = startTick;
    }

    public BattleRoyaleDataSyncS2CPacket(FriendlyByteBuf buf) {
        this.active = buf.readBoolean();
        int size = buf.readInt();
        this.players = new HashSet<>();
        for (int i = 0; i < size; i++) {
            this.players.add(buf.readUUID());
        }
        this.startTick = buf.readLong();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(active);
        buf.writeInt(players.size());
        for (UUID id : players) {
            buf.writeUUID(id);
        }
        buf.writeLong(startTick);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BattleRoyaleClientData.active = active;
            BattleRoyaleClientData.players.clear();
            BattleRoyaleClientData.players.addAll(players);
            BattleRoyaleClientData.startTick = startTick;
        });
        ctx.get().setPacketHandled(true);
    }
}