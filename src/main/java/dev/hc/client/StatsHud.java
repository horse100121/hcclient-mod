package dev.hc.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayDeque;
import java.util.Collection;

public class StatsHud {
    private static final int CYAN = 0xFF55FFFF;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int PANEL = 0xAA1A2A2A;

    private static final ArrayDeque<Long> leftClicks = new ArrayDeque<>();
    private static final ArrayDeque<Long> rightClicks = new ArrayDeque<>();
    private static boolean lastL, lastR;

    public static void poll(MinecraftClient mc) {
        if (mc.player == null || mc.options == null) return;
        long now = System.currentTimeMillis();

        boolean l = mc.options.attackKey.isPressed();
        boolean r = mc.options.useKey.isPressed();

        if (l && !lastL) leftClicks.add(now);
        if (r && !lastR) rightClicks.add(now);
        lastL = l;
        lastR = r;

        leftClicks.removeIf(t -> now - t > 1000);
        rightClicks.removeIf(t -> now - t > 1000);
    }

    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;

        int x = 6;
        int y = 6;

        drawLine(ctx, x, y, "FPS: " + mc.getCurrentFps());
        y += 14;

        drawLine(ctx, x, y, "L-CPS: " + leftClicks.size() + "   R-CPS: " + rightClicks.size());
        y += 14;

        if (HCClient.showCoords) {
            drawLine(ctx, x, y, String.format("XYZ: %.1f  %.1f  %.1f",
                    mc.player.getX(), mc.player.getY(), mc.player.getZ()));
            y += 14;
        }

        if (HCClient.showPing) {
            drawLine(ctx, x, y, "Ping: " + getPing(mc) + "ms");
            y += 14;
        }

        if (HCClient.showTotems) {
            drawLine(ctx, x, y, "Totems: " + countTotems(mc));
            y += 14;
        }

        if (HCClient.showArmor) {
            y += 4;
            renderArmor(ctx, x, y, mc);
            y += 30;
        }

        if (HCClient.showPotions) {
            y = renderPotions(ctx, x, y, mc);
        }

        if (HCClient.customScoreboard) {
            y += 6;
            renderCustomScoreboard(ctx, x, y, mc);
        }

        if (HCClient.showKeystrokes) {
            drawKeystrokes(ctx, 6, ctx.getScaledWindowHeight() - 70, mc);
        }
    }

    private static int countTotems(MinecraftClient mc) {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isOf(Items.TOTEM_OF_UNDYING)) count += s.getCount();
        }
        ItemStack off = mc.player.getOffHandStack();
        if (off.isOf(Items.TOTEM_OF_UNDYING)) count += off.getCount();
        return count;
    }

    private static void renderArmor(DrawContext ctx, int x, int y, MinecraftClient mc) {
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = mc.player.getEquippedStack(slots[i]);
            if (stack.isEmpty()) continue;
            int sx = x + i * 22;
            ctx.drawItem(stack, sx, y);
            if (stack.isDamageable()) {
                int left = stack.getMaxDamage() - stack.getDamage();
                ctx.drawText(mc.textRenderer, String.valueOf(left), sx, y + 18, WHITE, true);
            }
        }
    }

    private static int renderPotions(DrawContext ctx, int x, int y, MinecraftClient mc) {
        Collection<StatusEffectInstance> effects = mc.player.getStatusEffects();
        for (StatusEffectInstance effect : effects) {
            String name = effect.getEffectType().value().getName().getString();
            int amp = effect.getAmplifier() + 1;
            int seconds = effect.getDuration() / 20;
            drawLine(ctx, x, y, name + " " + amp + " (" + seconds + "s)");
            y += 14;
        }
        return y;
    }

    private static void drawLine(DrawContext ctx, int x, int y, String text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int w = mc.textRenderer.getWidth(text) + 12;
        ctx.fill(x, y, x + w, y + 13, PANEL);
        ctx.drawText(mc.textRenderer, text, x + 6, y + 3, CYAN, true);
    }

    private static void renderCustomScoreboard(DrawContext ctx, int x, int y, MinecraftClient mc) {
        int players = mc.getNetworkHandler() != null ? mc.getNetworkHandler().getPlayerList().size() : 0;
        String[] lines = {
                "\u00a7b\u00a7lHC Scoreboard",
                "\u00a77Players: " + players,
                "\u00a77Ping: " + getPing(mc) + "ms",
                "\u00a77FPS: " + mc.getCurrentFps()
        };
        int maxW = 0;
        for (String line : lines) maxW = Math.max(maxW, mc.textRenderer.getWidth(line));
        int panelW = maxW + 16;
        int panelH = lines.length * 12 + 8;
        ctx.fill(x, y, x + panelW, y + panelH, 0xCC0A1A1A);
        ctx.fill(x, y, x + panelW, y + 1, CYAN);

        int ty = y + 4;
        for (String line : lines) {
            ctx.drawText(mc.textRenderer, line, x + 8, ty, WHITE, true);
            ty += 12;
        }
    }

    private static int getPing(MinecraftClient mc) {
        if (mc.getNetworkHandler() == null || mc.player == null) return 0;
        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return entry != null ? entry.getLatency() : 0;
    }

    private static void drawKeystrokes(DrawContext ctx, int x, int y, MinecraftClient mc) {
        boolean w = mc.options.forwardKey.isPressed();
        boolean a = mc.options.leftKey.isPressed();
        boolean s = mc.options.backKey.isPressed();
        boolean d = mc.options.rightKey.isPressed();
        boolean space = mc.options.jumpKey.isPressed();

        int size = 20;
        drawKey(ctx, x + size, y, "W", w, 18);
        drawKey(ctx, x, y + size, "A", a, 18);
        drawKey(ctx, x + size, y + size, "S", s, 18);
        drawKey(ctx, x + size * 2, y + size, "D", d, 18);
        drawKey(ctx, x, y + size * 2, "SPACE", space, size * 3 - 2);
    }

    private static void drawKey(DrawContext ctx, int x, int y, String label, boolean pressed, int width) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int bg = pressed ? 0xFF00CCCC : 0xCC1A1A1A;
        int border = pressed ? CYAN : 0xFF333333;
        ctx.fill(x, y, x + width, y + 18, bg);
        ctx.fill(x, y, x + width, y + 1, border);
        ctx.fill(x, y + 17, x + width, y + 18, border);
        ctx.fill(x, y, x + 1, y + 18, border);
        ctx.fill(x + width - 1, y, x + width, y + 18, border);

        int textX = x + (width - mc.textRenderer.getWidth(label)) / 2;
        ctx.drawText(mc.textRenderer, label, textX, y + 5, WHITE, false);
    }
}
