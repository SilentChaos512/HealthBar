package net.silentchaos512.hpbar.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.hpbar.HealthBar;
import net.silentchaos512.hpbar.config.Color;
import net.silentchaos512.hpbar.config.Config;

public class GuiHealthBar extends Gui {

  public static final ResourceLocation TEXTURE_FRAME = new ResourceLocation(
      HealthBar.RESOURCE_PREFIX, "textures/frame.png");
  public static final ResourceLocation TEXTURE_BAR = new ResourceLocation(HealthBar.RESOURCE_PREFIX,
      "textures/bar.png");

  private Minecraft mc;

  public GuiHealthBar(Minecraft mc) {

    super();
    this.mc = mc;
  }

  @SubscribeEvent
  public void onRenderGameOverlay(RenderGameOverlayEvent event) {

    // Hide vanilla health?
    if (Config.replaceVanillaHealth && event.isCancelable() && event.getType() == ElementType.HEALTH) {
      event.setCanceled(true);
      GuiIngameForge.left_height += 10;
    }

    // Only render on TEXT (seems to cause problems in other cases).
    if (event.isCancelable() || event.getType() != ElementType.TEXT) {
      return;
    }

    // Don't render in creative mode?
    if (Config.replaceVanillaHealth && mc.player.capabilities.isCreativeMode) {
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

    ScaledResolution res = new ScaledResolution(mc);

    int posX, posY;
    float scale;

    final boolean replaceVanilla = Config.replaceVanillaHealth;
    final int barWidth = replaceVanilla ? 80 : Config.barWidth;
    final int barHeight = replaceVanilla ? 8 : Config.barHeight;
    final float xOffset = Config.xOffset;
    final float yOffset = Config.yOffset;

    /*
     * Render a health bar
     */
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_BLEND);

    scale = Config.barScale;
    if (scale > 0f) {
      GL11.glPushMatrix();

      // Quiver when low on health
      double quiverIntensity = Math.max(Config.barQuiverFraction - healthFraction + 0.01f, 0f)
          * Config.barQuiverIntensity / Config.barQuiverFraction;
      double quiverX = HealthBar.instance.random.nextGaussian() * quiverIntensity;
      double quiverY = HealthBar.instance.random.nextGaussian() * quiverIntensity;
      double quiverScale = HealthBar.instance.random.nextGaussian() * quiverIntensity * 0.05 + 1.0;
      scale *= quiverScale;

      if (replaceVanilla) {
        posX = (int) (res.getScaledWidth() / 2 - 91 + quiverX);
        posY = (int) (res.getScaledHeight() - GuiIngameForge.left_height + 20 + quiverY);
      } else {
        posX = (int) (res.getScaledWidth() / scale * xOffset - barWidth / 2 + quiverX);
        posY = (int) (res.getScaledHeight() / scale * yOffset + quiverY);
        GL11.glScalef(scale, scale, 1);
      }

      // Health bar
      drawBar(posX, posY, barWidth, barHeight, Config.colorHealthBar, healthFraction);
      // Bar frame
      drawBarFrame(posX, posY, barWidth, barHeight);

      GL11.glEnable(GL11.GL_BLEND);
      GL11.glPopMatrix();
    }

    /*
     * Render current/max health
     */

    scale = Config.textScale;
    scale = scale > 0f && replaceVanilla ? 0.8f : scale;
    if (scale > 0f) {
      GL11.glPushMatrix();

      FontRenderer fontRender = mc.fontRenderer;
      String format = Config.healthStringFormat;
      String str = String.format(format, currentHealth, maxHealth);
      // Add padding if current health has fewer digits than max.
      int extraSpaces = String.format("%.1f", maxHealth).length()
          - String.format("%.1f", currentHealth).length();
      for (int i = 0; i < extraSpaces; ++i) {
        str = " " + str;
      }
      int stringWidth = fontRender.getStringWidth(str);

      if (replaceVanilla) {
        final float paddingX = (barWidth - stringWidth * scale) / 2f;
        final float paddingY = (barHeight - fontRender.FONT_HEIGHT / scale) / 2f;
        posX = (int) (res.getScaledWidth() / 2 - 91 + paddingX);
        posY = (int) (res.getScaledHeight() - GuiIngameForge.left_height + 20 - paddingY);
        GL11.glTranslatef(posX, posY, 0); // y pos is a bit off for scale != 0.8f
        GL11.glScalef(scale, scale, 1);
        fontRender.drawStringWithShadow(str, 0, 0, 0xFFFFFF);
      } else {
        posX = (int) (res.getScaledWidth() / scale * xOffset - stringWidth / 2);
        posY = (int) (res.getScaledHeight() / scale * yOffset - 2) - barHeight;
        GL11.glScalef(scale, scale, 1);
        fontRender.drawStringWithShadow(str, posX, posY, 0xFFFFFF);
        // Last damage taken?
        if (Config.showLastDamageTaken) {
          str = String.format(Config.damageStringFormat, lastDamage);
          stringWidth = fontRender.getStringWidth(str);
          posX = (int) (res.getScaledWidth() / scale * xOffset - stringWidth / 2);
          posY = (int) (res.getScaledHeight() / scale * yOffset - 5 - fontRender.FONT_HEIGHT) - barHeight;
          fontRender.drawStringWithShadow(str, posX, posY, 0xFF4444);
        }
      }

      GL11.glPopMatrix();
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

  protected void renderFoodBar(RenderGameOverlayEvent event) {

    
  }

  protected void drawBar(float x, float y, float width, float height, Color color, float fraction) {

    mc.renderEngine.bindTexture(TEXTURE_BAR);
    GL11.glColor4f(color.red, color.green, color.blue, Config.barOpacity);
    float barPosWidth = width * fraction;
    float barPosX = x;
    if (Config.barJustification.equals("CENTER")) {
      barPosX += width * (1f - fraction) / 2;
    } else if (Config.barJustification.equals("RIGHT")) {
      barPosX += width * (1f - fraction);
    }
    drawRect(barPosX, y, 0, 0, barPosWidth, height);
  }

  protected void drawBarFrame(float x, float y, float width, float height) {

    mc.renderEngine.bindTexture(TEXTURE_FRAME);
    GL11.glColor4f(1f, 1f, 1f, Config.barOpacity);
    drawRect(x, y, 0, 0, width, height);
  }

  public void drawRect(float x, float y, float u, float v, float width, float height) {

    Tessellator tess = Tessellator.getInstance();
    BufferBuilder buff = tess.getBuffer();
    buff.begin(7, DefaultVertexFormats.POSITION_TEX);
    buff.pos(x, y + height, 0).tex(0, 1).endVertex();
    buff.pos(x + width, y + height, 0).tex(1, 1).endVertex();
    buff.pos(x + width, y, 0).tex(1, 0).endVertex();
    buff.pos(x, y, 0).tex(0, 0).endVertex();
    tess.draw();
  }
}
