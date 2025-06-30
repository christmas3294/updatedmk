package net.kaupenjoe.mccourse.talent;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.kaupenjoe.mccourse.event.TalentEvents;
import net.kaupenjoe.mccourse.talent.PlayerTalentData;
import net.kaupenjoe.mccourse.talent.Talent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.kaupenjoe.mccourse.event.TalentEvents;
import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.talent.PlayerTalentData;
import net.kaupenjoe.mccourse.talent.Talent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class TalentTreeScreen extends Screen {
    private final Player player;
    private static final ResourceLocation TEXTURE = new ResourceLocation(MCCourseMod.MOD_ID,
            "textures/gui/talent_tree.png");
    private static final int IMAGE_WIDTH = 128;
    private static final int IMAGE_HEIGHT = 128;

    public TalentTreeScreen(Player player) {
        super(Component.literal("Talent Tree"));
        this.player = player;
    }

    @Override
    protected void init() {
        int baseX = (width - IMAGE_WIDTH) / 2;
        int baseY = (height - IMAGE_HEIGHT) / 2;
        for (Talent talent : Talent.values()) {
            int x = baseX + talent.getGuiX();
            int y = baseY + talent.getGuiY();
            addRenderableWidget(new TalentIconButton(x, y,   new ResourceLocation(MCCourseMod.MOD_ID, talent.geticon().getPath()),talent.getName(), b -> {
                TalentEvents.applyTalents(player,b.getMessage().getString());
            }));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(TEXTURE, (width - IMAGE_WIDTH) / 2, (height - IMAGE_HEIGHT) / 2,
                0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        drawConnections(guiGraphics);
        drawValues(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void drawConnections(GuiGraphics guiGraphics) {
        drawLine(guiGraphics, Talent.STRENGTH, Talent.SPEED);
        drawLine(guiGraphics, Talent.STRENGTH, Talent.HEALTH);
    }

    private void drawLine(GuiGraphics guiGraphics, Talent a, Talent b) {
        int baseX = (width - IMAGE_WIDTH) / 2;
        int baseY = (height - IMAGE_HEIGHT) / 2;
        int ax = baseX + a.getGuiX() + 30;
        int ay = baseY + a.getGuiY() + 10;
        int bx = baseX + b.getGuiX() + 30;
        int by = baseY + b.getGuiY() + 10;

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(ax, ay, 0).color(255, 255, 255, 255).endVertex();
        buffer.vertex(bx, by, 0).color(255, 255, 255, 255).endVertex();
        Tesselator.getInstance().end();
    }

    private void drawValues(GuiGraphics guiGraphics) {
        int baseX = (width - IMAGE_WIDTH) / 2;
        int baseY = (height - IMAGE_HEIGHT) / 2;
        for (Talent talent : Talent.values()) {
            var attr = player.getAttribute(talent.getAttribute());
            if (attr == null) continue;
            double value = attr.getValue();
            String text = talent.getName();
            guiGraphics.drawString(Minecraft.getInstance().font, text,
                    baseX + talent.getGuiX() + 22,
                    baseY + talent.getGuiY() + 22, 0xFFFFFF, false);
        }
        int points = PlayerTalentData.getPoints(player);
        guiGraphics.drawString(Minecraft.getInstance().font, "Points: " + points,
                baseX + 2, baseY + 2, 0xFFFFFF, false);
    }

    private static class TalentIconButton extends Button {
        private final ResourceLocation icon;

        public TalentIconButton(int x, int y, ResourceLocation icon,String name, OnPress press) {
            super(x, y, 20, 20, Component.empty(), press, Button.DEFAULT_NARRATION);
            this.icon = icon;
            setMessage(Component.nullToEmpty(name));
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            guiGraphics.blit(icon, getX() + 2, getY() + 2, 0, 0, 16, 16);
        }
    }
}
