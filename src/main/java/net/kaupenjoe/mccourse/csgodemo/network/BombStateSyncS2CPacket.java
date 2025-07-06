package net.kaupenjoe.mccourse.csgodemo.network;


import net.kaupenjoe.mccourse.csgodemo.client.BombClientData;
import net.kaupenjoe.mccourse.csgodemo.team.BombTeam;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Packet for syncing bomb mode state to clients.
 */
//服务器的数据 同步到所有客户端
public class BombStateSyncS2CPacket {
    private final boolean bombPlanted;
    private final Map<UUID, BombTeam> players;
   // private  Map<UUID, BombTeam> deadPlayers;


    public BombStateSyncS2CPacket(boolean bombPlanted, Map<UUID, BombTeam> players) {
        this.bombPlanted = bombPlanted;
        this.players = new HashMap<>(players);
     //   this.deadPlayers = new HashMap<>(players);
    }

//    public BombStateSyncS2CPacket(boolean bombPlanted, Map<UUID, BombTeam> players,Map<UUID, BombTeam> deadPlayers) {
//        this.bombPlanted = bombPlanted;
//        this.players = new HashMap<>(players);
//      //  this.deadPlayers = deadPlayers;
//
//    }
    //客户端通过这个构造器解构服务器写入的数据
    public BombStateSyncS2CPacket(FriendlyByteBuf buf) {
        this.bombPlanted = buf.readBoolean();
       // this.deadPlayers = buf.readMap()
        int size = buf.readInt();
        this.players = new HashMap<>();
        for (int i = 0; i < size; i++) {
            UUID id = buf.readUUID();
            BombTeam team = BombTeam.values()[buf.readVarInt()];
            this.players.put(id, team);
        }
    }
//服务器写入数据给客户端读取
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(bombPlanted);
        buf.writeInt(players.size());
        for (Map.Entry<UUID, BombTeam> entry : players.entrySet()) {
            buf.writeUUID(entry.getKey());
            buf.writeVarInt(entry.getValue().ordinal());
        }
//        for (Map.Entry<UUID, BombTeam> entry : deadPlayers.entrySet()) {
//            buf.writeUUID(entry.getKey());
//            buf.writeVarInt(entry.getValue().ordinal());
//        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BombClientData.bombPlanted = bombPlanted;
            BombClientData.players.clear();
            BombClientData.players.putAll(players);
        });
        ctx.get().setPacketHandled(true);
    }
}

