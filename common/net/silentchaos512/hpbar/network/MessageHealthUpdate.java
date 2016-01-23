package net.silentchaos512.hpbar.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.hpbar.HealthBar;

public class MessageHealthUpdate implements IMessage {

  private float currentHealth;
  private float maxHealth;

  public MessageHealthUpdate() {

  }

  public MessageHealthUpdate(float current, float max) {

    this.currentHealth = current;
    this.maxHealth = max;
  }

  @Override
  public void fromBytes(ByteBuf buf) {

    this.currentHealth = buf.readFloat();
    this.maxHealth = buf.readFloat();
  }

  @Override
  public void toBytes(ByteBuf buf) {

    buf.writeFloat(currentHealth);
    buf.writeFloat(maxHealth);
  }

  public static class Handler implements IMessageHandler<MessageHealthUpdate, IMessage> {

    @Override
    public IMessage onMessage(MessageHealthUpdate message, MessageContext ctx) {

      if (ctx.side == Side.CLIENT) {
        HealthBar.instance.setPlayerHealth(message.currentHealth);
        HealthBar.instance.setPlayerMaxHealth(message.maxHealth);

        // Fix the player's health.
//        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
//        player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
//            .setBaseValue(message.maxHealth);
//        player.setHealth(message.currentHealth);
      }
      return null;
    }
  }
}
