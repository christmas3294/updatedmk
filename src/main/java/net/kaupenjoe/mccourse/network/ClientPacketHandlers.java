package net.kaupenjoe.mccourse.network;

import net.kaupenjoe.mccourse.client.TalentScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandlers {
    public static void openTalentScreen() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.setScreen(new TalentScreen());
        }
    }
}