package net.kaupenjoe.mccourse.event;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.talent.TalentTreeScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MCCourseMod.MOD_ID, value = Dist.CLIENT)
public class ClientTalentEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
//        if (MCCourseMod.ClientModEvents.OPEN_TALENT_KEY != null && MCCourseMod.ClientModEvents.OPEN_TALENT_KEY.consumeClick()) {
//            Minecraft mc = Minecraft.getInstance();
//            if (mc.player != null) {
//                mc.setScreen(new TalentTreeScreen(mc.player));
//            }
//        }
    }
}