package dev.hc.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class SettingsScreen extends Screen {
    private static final String GREEN = "\u00a7a";
    private static final String GRAY = "\u00a77";

    public SettingsScreen() {
        super(Text.literal("HC Client"));
    }

    @Override
    protected void init() {
        int bw = 150;
        int bh = 20;
        int gap = 4;
        int leftX = this.width / 2 - bw - gap / 2;
        int rightX = this.width / 2 + gap / 2;
        int y = 50;
        int col = 0;

        col = addToggle(col, leftX, rightX, y, bw, bh, "Fullbright", () -> HCClient.fullbrightEnabled, HCClient::setFullbright);
        y = nextY(col, y, bh, gap);
        col = addToggle(col, leftX, rightX, y, bw, bh, "HUD", () -> HCClient.hudEnabled, v -> HCClient.hudEnabled = v);
        y = nextY(col, y, bh, gap);
        col = addToggle(col, leftX, rightX, y, bw, bh, "Coordinates", () -> HCClient.showCoords, v -> HCClient.showCoords = v);
        y = nextY(col, y, bh, gap);
        col = addToggle(col, leftX, rightX, y, bw, bh, "Ping", () -> HCClient.showPing, v -> HCClient.showPing = v);
        y = nextY(col, y, bh, gap);
        col = addToggle(col, leftX, rightX, y, bw, bh, "Armor Display", () -> HCClient.showArmor, v -> HCClient.showArmor = v);
        y = nextY(col, y, bh, gap);
        col = addToggle(col, leftX, rightX, y, bw, bh, "Potion Effects", () -> HCClient.showPotions, v -> HCClient.showPotions = v);
        y = nextY(col, y, bh, gap);
        col = addToggle(col, leftX, rightX, y, bw, bh, "Totem Counter", () -> HCClient.showTotems, v -> HCClient.showTotems = v);
        y = nextY(col, y, bh, gap);
        col = addToggle(col, leftX, rightX, y, bw, bh, "Keystrokes", () -> HCClient.showKeystrokes, v -> HCClient.showKeystrokes = v);
        y = nextY(col, y, bh, gap);
        col = addToggle(col, leftX, rightX, y, bw, bh, "Animated Hotbar", () -> HCClient.hotbarAnim, v -> HCClient.hotbarAnim = v);
        y = nextY(col, y, bh, gap);
        col = addToggle(col, leftX, rightX, y, bw, bh, "Watermark", () -> HCClient.showWatermark, v -> HCClient.showWatermark = v);
        y = nextY(col, y, bh, gap);
        col = addToggle(col, leftX, rightX, y, bw, bh, "Scoreboard", () -> HCClient.showScoreboard, v -> HCClient.showScoreboard = v);
        y = nextY(col, y, bh, gap);
        col = addToggle(col, leftX, rightX, y, bw, bh, "Custom Scoreboard", () -> HCClient.customScoreboard, v -> HCClient.customScoreboard = v);
        y = nextY(col, y, bh, gap);

        int fullW = bw * 2 + gap;
        this.addDrawableChild(ButtonWidget.builder(scaleLabel(), button -> {
            if (HCClient.scoreboardScale >= 1.5f) HCClient.scoreboardScale = 0.6f;
            else HCClient.scoreboardScale = Math.round((HCClient.scoreboardScale + 0.1f) * 10f) / 10f;
            button.setMessage(scaleLabel());
        }).dimensions(leftX, y, fullW, bh).build());
        y += bh + gap + 6;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("\u00a7cClose"), button -> this.close())
                .dimensions(leftX, y, fullW, bh).build());
    }

    private static Text scaleLabel() {
        return Text.literal("Scoreboard Scale: " + String.format("%.1f", HCClient.scoreboardScale));
    }

    /** Adds a toggle in the left (col 0) or right (col 1) column, returns the next column. */
    private int addToggle(int col, int leftX, int rightX, int y, int w, int h, String name,
                          BooleanSupplier getter, Consumer<Boolean> setter) {
        int x = col == 0 ? leftX : rightX;
        this.addDrawableChild(ButtonWidget.builder(label(name, getter.getAsBoolean()), button -> {
            boolean newVal = !getter.getAsBoolean();
            setter.accept(newVal);
            button.setMessage(label(name, newVal));
        }).dimensions(x, y, w, h).build());
        return col == 0 ? 1 : 0;
    }

    /** After placing a toggle: if the next column is 0 we finished a row, so move down. */
    private int nextY(int nextCol, int y, int h, int gap) {
        return nextCol == 0 ? y + h + gap : y;
    }

    private static Text label(String name, boolean enabled) {
        return Text.literal(name + ": " + (enabled ? GREEN + "ON" : GRAY + "OFF"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("\u00a7b\u00a7lHC CLIENT"), this.width / 2, 22, 0xFF55FFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("\u00a77Settings"), this.width / 2, 34, 0xFFAAAAAA);
    }
}
