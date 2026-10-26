package dev.hc.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class LogoOverlay {
    private static final Identifier LOGO = Identifier.of("hcclient", "textures/gui/hc_logo.png");

    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getWindow() == null || mc.options.hudHidden) return;
        int w = 40;
        int h = 31; // keeps the 128x100 aspect ratio
        int x = ctx.getScaledWindowWidth() - w - 5;
        int y = 5;
        // x, y, u, v, drawW, drawH, regionW, regionH, textureW, textureH
        ctx.drawTexture(RenderPipelines.GUI_TEXTURED, LOGO, x, y, 0f, 0f, w, h, 128, 100, 128, 100);
    }
}
