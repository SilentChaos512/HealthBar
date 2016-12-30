package net.silentchaos512.hpbar;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.hpbar.config.Config;
import net.silentchaos512.hpbar.gui.GuiHealthBar;
import net.silentchaos512.hpbar.network.MessageHealthUpdate;
import net.silentchaos512.hpbar.proxy.HealthBarCommonProxy;

//@formatter:off
@Mod(modid = HealthBar.MOD_ID,
    name = HealthBar.MOD_NAME,
    version = HealthBar.VERSION_NUMBER,
    guiFactory = "net.silentchaos512.hpbar.gui.GuiFactoryHealthBar",
    updateJSON = "https://raw.githubusercontent.com/SilentChaos512/HealthBar/master/update.json")
//@formatter:on
public class HealthBar {

  public static final String MOD_ID = "HealthBar";
  public static final String MOD_NAME = "Health Bar";
  public static final String VERSION_NUMBER = "@VERSION@";
  public static final String CHANNEL_NAME = MOD_ID;
  public static final String RESOURCE_PREFIX = MOD_ID.toLowerCase();

  public static final float CLIENT_MODE_DELAY = 5000;

  private float playerCurrentHealth = 20f;
  private float playerMaxHealth = 20f;
  private float playerPrevCurrentHealth = 20f;
  private float playerPrevMaxHealth = 20f;
  private float playerLastDamageTaken = 0f;
  private long lastUpdatePacketTime = 0L;

  @Instance(MOD_ID)
  public static HealthBar instance;

  @SidedProxy(clientSide = "net.silentchaos512.hpbar.proxy.HealthBarClientProxy", serverSide = "net.silentchaos512.hpbar.proxy.HealthBarCommonProxy")
  public static HealthBarCommonProxy proxy;

  public static SimpleNetworkWrapper network;

  public static Random random = new Random();

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    Config.init(event.getSuggestedConfigurationFile());
    Config.save();

    network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
    int discriminator = -1;
    network.registerMessage(MessageHealthUpdate.Handler.class, MessageHealthUpdate.class,
        discriminator, Side.CLIENT);

    MinecraftForge.EVENT_BUS.register(this);
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {

    if (event.getSide() == Side.CLIENT) {
      MinecraftForge.EVENT_BUS.register(new GuiHealthBar(Minecraft.getMinecraft()));
    }
  }

  @SubscribeEvent
  public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {

    if (event.getModID().equals(MOD_ID)) {
      Config.load();
      Config.save();
    }
  }

  @SubscribeEvent
  public void onEntityConstructing(EntityConstructing event) {

    if (event.getEntity() instanceof EntityPlayerMP) {
      EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
      if (player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH) != null) {
        float current = player.getHealth();
        float max = player.getMaxHealth();
        network.sendTo(new MessageHealthUpdate(current, max), player);
      }
    }
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {

    // Send a health update packet to the player if necessary.
    if (event.player instanceof EntityPlayerMP) {
      float current = event.player.getHealth();
      float max = event.player.getMaxHealth();

      boolean healthChanged = current != playerPrevCurrentHealth || max != playerPrevMaxHealth;
      boolean checkInTime = Config.checkinFrequency <= 0 ? false
          : event.player.world.getTotalWorldTime() % Config.checkinFrequency == 0;
      if (healthChanged || checkInTime) {
        // Calculate health change, save the number if damage was taken.
        float diff = current - playerPrevCurrentHealth;
        if (diff < 0)
          playerLastDamageTaken = -diff;

        network.sendTo(new MessageHealthUpdate(current, max), (EntityPlayerMP) event.player);
      }
    }
  }

  public float getPlayerHealth() {

    if (System.currentTimeMillis() - lastUpdatePacketTime > CLIENT_MODE_DELAY) {
      EntityPlayer clientPlayer = proxy.getClientPlayer();
      if (clientPlayer != null)
        return clientPlayer.getHealth();
    }

    return playerCurrentHealth;
  }

  public float getPlayerMaxHealth() {

    if (System.currentTimeMillis() - lastUpdatePacketTime > CLIENT_MODE_DELAY) {
      EntityPlayer clientPlayer = proxy.getClientPlayer();
      if (clientPlayer != null)
        return clientPlayer.getMaxHealth();
    }
    return playerMaxHealth;
  }

  public float getPlayerLastDamageTaken() {

    return playerLastDamageTaken;
  }

  public void handleUpdatePacket(float health, float maxHealth) {

    playerPrevCurrentHealth = playerCurrentHealth;
    playerPrevMaxHealth = playerMaxHealth;
    playerCurrentHealth = health;
    playerMaxHealth = maxHealth;
    lastUpdatePacketTime = System.currentTimeMillis();
  }
}
