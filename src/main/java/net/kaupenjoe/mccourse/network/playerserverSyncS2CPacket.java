package net.kaupenjoe.mccourse.network;

import net.kaupenjoe.mccourse.battleroyale.BattleRoyaleCommand;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class playerserverSyncS2CPacket {
    public   HashMap<String, ServerPlayer> playerHashMap;

    public playerserverSyncS2CPacket(ServerPlayer playerHashMap,HashMap<String, ServerPlayer> playerHashMap1) {
        this.playerHashMap = playerHashMap1;

        this.playerHashMap.put(playerHashMap.getUUID().toString(), playerHashMap);
    }
    public playerserverSyncS2CPacket(FriendlyByteBuf buf) {
        // no data
//        this.skillnbt = buf.readNbt();
    }

    public playerserverSyncS2CPacket() {

    }

    public void toBytes(FriendlyByteBuf buf) {
        // no data
//        new
//        buf.writeNbt(playerHashMap);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BattleRoyaleCommand.playerserver = playerHashMap;
          //  DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> ClientPacketHandlers.openTalentScreen(skillnbt, player));

        });
        ctx.get().setPacketHandled(true);
    }



}
