package net.kaupenjoe.mccourse.network;

import net.kaupenjoe.mccourse.nbt.PlayerSkillData;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenTalentScreenS2CPacket {
    CompoundTag skillnbt;
    public OpenTalentScreenS2CPacket(CompoundTag data) {
        this.skillnbt = data;
    }


    public OpenTalentScreenS2CPacket(FriendlyByteBuf buf) {
        // no data
    }

    public OpenTalentScreenS2CPacket() {

    }

    public void toBytes(FriendlyByteBuf buf) {
        // no data
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandlers.openTalentScreen(skillnbt));

        });
        ctx.get().setPacketHandled(true);
    }




}