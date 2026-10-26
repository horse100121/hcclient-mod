package dev.hc.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class HCClient implements ClientModInitializer {
    public static final String MOD_ID = "hcclient";

    public static KeyBinding fullbrightKey;
    public static KeyBinding hudToggleKey;
    public static KeyBinding menuKey;

    public static boolean fullbrightEnabled = false;
    public static boolean hudEnabled = true;
    public static boolean showTotems = true;
    public static boolean showKeystrokes = true;
    public static boolean hotbarAnim = true;
    public static boolean showWatermark = true;
    public static boolean showCoords = true;
    public static boolean showPing = true;
    public static boolean showArmor = true;
    public static boolean showPotions = true;
    public static boolean showScoreboard = true;
    public static boolean customScoreboard = false;
    public static float scoreboardScale = 1.0f;

    @Override
    public void onInitializeClient() {
        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));

        fullbrightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hcclient.fullbright", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, category));

        hudToggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hcclient.hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, category));

        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hcclient.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) {
                client.setScreen(new SettingsScreen());
            }

            while (fullbrightKey.wasPressed()) {
                setFullbright(!fullbrightEnabled);
            }

            while (hudToggleKey.wasPressed()) {
                hudEnabled = !hudEnabled;
            }

            StatsHud.poll(client);
        });

        // Our own HUD (stats, logo, hotbar animation)
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            if (!hudEnabled) return;
            StatsHud.render(drawContext);
            if (showWatermark) LogoOverlay.render(drawContext);
            if (hotbarAnim) HotbarAnim.render(drawContext);
        });

        // Wrap the vanilla scoreboard: allows hiding it and scaling it (no mixin needed)
        HudElementRegistry.replaceElement(VanillaHudElements.SCOREBOARD, original -> (ctx, tickCounter) -> {
            if (!showScoreboard) return;
            float s = scoreboardScale;
            if (s == 1.0f) {
                original.render(ctx, tickCounter);
                return;
            }
            float w = ctx.getScaledWindowWidth();
            float h = ctx.getScaledWindowHeight() / 2f;
            ctx.getMatrices().pushMatrix();
            ctx.getMatrices().translate(w, h);
            ctx.getMatrices().scale(s, s);
            ctx.getMatrices().translate(-w, -h);
            original.render(ctx, tickCounter);
            ctx.getMatrices().popMatrix();
        });
    }

    public static void setFullbright(boolean on) {
        fullbrightEnabled = on;
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.options != null) {
            mc.options.getGamma().setValue(on ? 16.0 : 1.0);
        }
    }
}
