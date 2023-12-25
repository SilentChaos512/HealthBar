package net.silentchaos512.hpbar.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.hpbar.HealthBar;
import net.silentchaos512.hpbar.config.Color;
import net.silentchaos512.hpbar.config.Config;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = HealthBar.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HealthRenderer extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(HealthBar.MOD_ID, "textures/health_bar.png");

    private final Minecraft mc;

    public HealthRenderer(Minecraft mc) {
        super(Component.empty());
        this.mc = mc;
    }

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGuiOverlayEvent.Pre event) {
        // Hide vanilla health?
        if (Config.replaceVanillaHealth && event.isCancelable() && event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type()) {
            event.setCanceled(true);
            getGui().leftHeight += 10;
        }

        if (event.getOverlay() != VanillaGuiOverlay.DEBUG_TEXT.type()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        // Don't render in creative mode?
        if (Config.replaceVanillaHealth && mc.player.getAbilities().instabuild) {
            return;
        }

        float currentHealth = HealthBar.instance.getPlayerHealth();
        float maxHealth = HealthBar.instance.getPlayerMaxHealth();
        float lastDamage = HealthBar.instance.getPlayerLastDamageTaken();
        float healthFraction = currentHealth / maxHealth;

        // Hide at full health
        if (healthFraction >= 1f && !Config.barShowAlways && !Config.replaceVanillaHealth) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();
        PoseStack pose = graphics.pose();
        RenderSystem.enableBlend();

        int posX, posY;
        float scale;
        int scaledWindowWidth = mc.getWindow().getGuiScaledWidth();
        int scaledWindowHeight = mc.getWindow().getGuiScaledHeight();

        final boolean replaceVanilla = Config.replaceVanillaHealth;
        final int barWidth = replaceVanilla ? 80 : 96;
        final int barHeight = replaceVanilla ? 8 : 16;
        final float xOffset = Config.xOffset;
        final float yOffset = Config.yOffset;

        /*
         * Render a health bar
         */
//        GL11.glDisable(GL11.GL_LIGHTING);
//        GL11.glEnable(GL11.GL_BLEND);
//        RenderSystem.enableBlend();

        scale = Config.barScale * 0.5f;
        if (scale > 0f) {
            pose.pushPose();

            // Quiver when low on health
            // TODO: Quiver briefly after taking damage?
            double quiverIntensity = Math.max(Config.barQuiverFraction - healthFraction + 0.01f, 0f)
                    * Config.barQuiverIntensity / Config.barQuiverFraction;
            double quiverX = HealthBar.instance.random.nextGaussian() * quiverIntensity;
            double quiverY = HealthBar.instance.random.nextGaussian() * quiverIntensity;
            double quiverScale = HealthBar.instance.random.nextGaussian() * quiverIntensity * 0.05 + 1.0;
            scale *= quiverScale;

            if (replaceVanilla) {
                posX = (int) (scaledWindowWidth / 2 - 91 + quiverX);
                posY = (int) (scaledWindowHeight - getGui().leftHeight + 20 + quiverY);
            } else {
                posX = (int) (scaledWindowWidth / scale * xOffset - barWidth / 2 + quiverX);
                posY = (int) (scaledWindowHeight / scale * yOffset + quiverY);
                pose.scale(scale, scale, 1);
            }

            // Bar frame
            drawBarFrame(graphics, posX, posY);
            // Health bar
            drawBar(graphics, posX, posY, barWidth, barHeight, Config.colorHealthBar, healthFraction);

            pose.popPose();
        }

        /*
         * Render current/max health
         */

        scale = Config.textScale * 0.7f;
        scale = scale > 0f && replaceVanilla ? 0.8f : scale;
        if (scale > 0f) {
            pose.pushPose();

            String format = Config.healthStringFormat;
            String str = String.format(format, currentHealth, maxHealth);
            // Add padding if current health has fewer digits than max.
            int extraSpaces = String.format("%.1f", maxHealth).length()
                    - String.format("%.1f", currentHealth).length();
            for (int i = 0; i < extraSpaces; ++i) {
                str = " " + str;
            }
            int stringWidth = mc.font.width(str);

            if (replaceVanilla) {
                final float paddingX = (barWidth - stringWidth * scale) / 2f;
                final float paddingY = (barHeight - mc.font.lineHeight / scale) / 2f;
                posX = (int) (scaledWindowWidth / 2 - 91 + paddingX);
                posY = (int) (scaledWindowHeight - getGui().leftHeight + 20 - paddingY);
                pose.translate(posX, posY, 0); // y pos is a bit off for scale != 0.8f
                pose.scale(scale, scale, 1);
                graphics.drawString(mc.font, str, 0, 0, 0xFFFFFF);
            } else {
                posX = (int) (scaledWindowWidth / scale * xOffset - stringWidth / 2);
                posY = (int) (scaledWindowHeight / scale * yOffset - 2) - barHeight;
                pose.scale(scale, scale, 1);
                graphics.drawString(mc.font, str, posX, posY, 0xFFFFFF);
                // Last damage taken?
                if (Config.showLastDamageTaken) {
                    str = String.format(Config.damageStringFormat, lastDamage);
                    stringWidth = mc.font.width(str);
                    posX = (int) (scaledWindowWidth / scale * xOffset - stringWidth / 2);
                    posY = (int) (scaledWindowHeight / scale * yOffset - 5 - mc.font.lineHeight) - barHeight;
                    graphics.drawString(mc.font, str, posX, posY, 0xFF4444);
                }
            }

            pose.popPose();
        }

        /*
         * FOOD BAR TEST
         */

//    posX = 5;
//    posY = 5;
//    scale = 1f;
//
//    int currentFood = mc.thePlayer.getFoodStats().getFoodLevel();
//    int maxFood = 20;
//    float currentSaturation = mc.thePlayer.getFoodStats().getSaturationLevel();
//    float maxSaturation = currentFood;
//
//    GL11.glPushMatrix();
//    drawBar(posX, posY, barWidth, barHeight, new Color(1f, 0.5f, 0f), (float) currentFood / maxFood);
//    drawBar(posX, posY + 3f / 4f * barHeight, barWidth, barHeight / 4f, new Color(1f, 0f, 1f), currentSaturation / maxSaturation);
//    drawBarFrame(posX, posY, barWidth, barHeight);
//    FontRenderer fontRender = mc.fontRendererObj;
//    String test = "%d (%.1f)";
//    test = String.format(test, currentFood, currentSaturation);
//    fontRender.drawStringWithShadow(test, posX, posY + barHeight, 0xFFFFFF);
//    GL11.glPopMatrix();

        //renderFoodBar(event);
    }

    protected void renderFoodBar(RenderGuiOverlayEvent.Pre event) {
    }

    private static void drawBar(GuiGraphics graphics, int x, int y, int width, int height, Color color, float fraction) {
        int barPosWidth = (int) (width * fraction);
        int barPosX = x;
        if (Config.barJustification.equals("CENTER")) {
            barPosX += width * (1f - fraction) / 2;
        } else if (Config.barJustification.equals("RIGHT")) {
            barPosX += width * (1f - fraction);
        }
        int barColor = ((int) (Config.barOpacity * 0xFF)) << 24 | color.intValue();
        blitWithColor(graphics, barPosX, y, 16, 5, barPosWidth, height, barColor);
    }

    private static void drawBarFrame(GuiGraphics graphics, int x, int y) {
        int color = ((int) ((Config.barOpacity * 0xFF)) << 24) | 0xFFFFFF;
        blitWithColor(graphics, x - 16, y - 5, 0, 22, 128, 48, color);
    }

    public static ForgeGui getGui() {
        return (ForgeGui) Minecraft.getInstance().gui;
    }

    private static void blitWithColor(GuiGraphics graphics, int x, int y, int textureX, int textureY, int width, int height, int color) {
        float a = ((color >> 24) & 255) / 255f;
        if (a <= 0f)
            a = 1f;
        float r = ((color >> 16) & 255) / 255f;
        float g = ((color >> 8) & 255) / 255f;
        float b = (color & 255) / 255f;
        graphics.setColor(r, g, b, a);
        graphics.blit(TEXTURE, x, y, textureX, textureY, width, height);
        graphics.setColor(1, 1, 1, 1);
    }
}
