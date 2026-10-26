package dev.hc.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Pulsing cyan outline around the selected hotbar slot. */
public class HotbarAnim {
    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;

        double t = (System.currentTimeMillis() % 1500L) / 1500.0;
        int alpha = 90 + (int) (120 * (0.5 + 0.5 * Math.sin(t * Math.PI * 2)));
        int color = (alpha << 24) | 0x55FFFF;

        int slot = mc.player.getInventory().getSelectedSlot();
        int x = ctx.getScaledWindowWidth() / 2 - 91 + slot * 20;
        int y = ctx.getScaledWindowHeight() - 22;

        // 24x24 outline (selected slot frame)
        ctx.fill(x - 1, y - 1, x + 23, y, color);
        ctx.fill(x - 1, y + 22, x + 23, y + 23, color);
        ctx.fill(x - 1, y, x, y + 22, color);
        ctx.fill(x + 22, y, x + 23, y + 22, color);
    }
}
