package net.silentchaos512.hpbar;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
    if (Config.hideVanillaHealth && event.isCancelable() && event.type == ElementType.HEALTH) {
      event.setCanceled(true);
    }

    // Only render on TEXT (seems to cause problems in other cases).
    if (event.isCancelable() || event.type != ElementType.TEXT) {
      return;
    }

    float currentHealth = HealthBar.instance.getPlayerHealth();
    float maxHealth = HealthBar.instance.getPlayerMaxHealth();
    float healthFraction = currentHealth / maxHealth;

    // Hide at full health
    if (healthFraction >= 1f && !Config.barShowAlways) {
      return;
    }

    ScaledResolution res = new ScaledResolution(mc);

    int posX, posY;
    float scale;

    final int barWidth = Config.barWidth;
    final int barHeight = Config.barHeight;
    final float xOffset = Config.xOffset;
    final float yOffset = Config.yOffset;

    /*
     * Render a health bar
     */
    GL11.glColor4f(1f, 1f, 1f, Config.barOpacity);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_BLEND);

    scale = Config.barScale;
    if (scale > 0f) {
      GL11.glPushMatrix();
      GL11.glScalef(scale, scale, 1);

      // Quiver when low on health
      double quiverIntensity = Math.max(Config.barQuiverFraction - healthFraction, 0f)
          * Config.barQuiverIntensity / Config.barQuiverFraction;
      double quiverX = HealthBar.instance.random.nextGaussian() * quiverIntensity;
      double quiverY = HealthBar.instance.random.nextGaussian() * quiverIntensity;

      posX = (int) (res.getScaledWidth() / scale * xOffset - barWidth / 2 + quiverX);
      posY = (int) (res.getScaledHeight() / scale * yOffset + quiverY);

      // Health bar
      mc.renderEngine.bindTexture(TEXTURE_BAR);
      float barPosWidth = barWidth * healthFraction;
      float barPosX = posX + barWidth * (1f - healthFraction) / 2;
      drawRect(barPosX, posY, 0, 0, barPosWidth, barHeight);

      // Bar frame
      mc.renderEngine.bindTexture(TEXTURE_FRAME);
      drawRect(posX, posY, 0, 0, barWidth, barHeight);

      GL11.glEnable(GL11.GL_BLEND);
      GL11.glPopMatrix();
    }

    /*
     * Render current/max health
     */

    scale = Config.textScale;
    if (scale > 0f) {
      GL11.glPushMatrix();
      GL11.glScalef(scale, scale, 1);

      FontRenderer fontRender = mc.fontRendererObj;
      String format = Config.healthStringFormat;
      String str = String.format(format, currentHealth, maxHealth);

      posX = (int) (res.getScaledWidth() / scale * xOffset - fontRender.getStringWidth(str) / 2);
      posY = (int) (res.getScaledHeight() / scale * yOffset - 2) - barHeight;
      fontRender.drawStringWithShadow(str, posX, posY, 0xFFFFFF);

      GL11.glPopMatrix();
    }
  }

  public void drawRect(float x, float y, float u, float v, float width, float height) {

    Tessellator tess = Tessellator.getInstance();
    WorldRenderer worldRender = tess.getWorldRenderer();
    worldRender.begin(7, DefaultVertexFormats.POSITION_TEX);
    worldRender.pos(x, y + height, 0).tex(0, 1).endVertex();
    worldRender.pos(x + width, y + height, 0).tex(1, 1).endVertex();
    worldRender.pos(x + width, y, 0).tex(1, 0).endVertex();
    worldRender.pos(x, y, 0).tex(0, 0).endVertex();
    tess.draw();
  }
}
