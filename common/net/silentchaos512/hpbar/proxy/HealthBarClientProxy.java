package net.silentchaos512.hpbar.proxy;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.client.Minecraft;

public class HealthBarClientProxy extends HealthBarCommonProxy {

  @Override
  public EntityPlayer getClientPlayer() {

    return Minecraft.getMinecraft().thePlayer;
  }
}
