package net.silentchaos512.hpbar.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class HealthBarClientProxy extends HealthBarCommonProxy {

  @Override
  public EntityPlayer getClientPlayer() {

    return Minecraft.getMinecraft().player;
  }
}
