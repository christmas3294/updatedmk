package net.kaupenjoe.mccourse.network;

import net.kaupenjoe.mccourse.nbt.PlayerSkillData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenTalentScreenS2CPacket {
    CompoundTag skillnbt;
    ServerPlayer player;
    public OpenTalentScreenS2CPacket(CompoundTag data, ServerPlayer player) {
        this.skillnbt = data;
        this.player = player;
    }


    public OpenTalentScreenS2CPacket(FriendlyByteBuf buf) {
        // no data
        this.skillnbt = buf.readNbt();
    }

    public OpenTalentScreenS2CPacket() {

    }

    public void toBytes(FriendlyByteBuf buf) {
        // no data
        buf.writeNbt(skillnbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandlers.openTalentScreen(skillnbt, player));

        });
        ctx.get().setPacketHandled(true);
    }




}