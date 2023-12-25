package net.silentchaos512.hpbar;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.hpbar.config.Config;

import java.util.Random;

@Mod(HealthBar.MOD_ID)
public class HealthBar {

    public static final String MOD_ID = "healthbar";

    public static final float CLIENT_MODE_DELAY = 5000;

    private float playerCurrentHealth = 20f;
    private float playerMaxHealth = 20f;
    private float playerPrevCurrentHealth = 20f;
    private float playerPrevMaxHealth = 20f;
    private float playerLastDamageTaken = 0f;
    private long lastUpdatePacketTime = 0L;

    public static HealthBar instance;

    public static SideProxy proxy;

    public static Random random = new Random();

    public HealthBar() {
        instance = this;
        proxy = DistExecutor.unsafeRunForDist(() -> SideProxy.Client::new, () -> SideProxy.Server::new);
    }

    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            if (player.getAttribute(Attributes.MAX_HEALTH) != null) {
                float current = player.getHealth();
                float max = player.getMaxHealth();
//                network.sendTo(new MessageHealthUpdate(current, max), player);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Send a health update packet to the player if necessary.
        if (event.player instanceof ServerPlayer) {
            float current = event.player.getHealth();
            float max = event.player.getMaxHealth();

            boolean healthChanged = current != playerPrevCurrentHealth || max != playerPrevMaxHealth;
            boolean checkInTime = Config.checkinFrequency > 0 && event.player.level().getGameTime() % Config.checkinFrequency == 0;
            if (healthChanged || checkInTime) {
                // Calculate health change, save the number if damage was taken.
                float diff = current - playerPrevCurrentHealth;
                if (diff < 0)
                    playerLastDamageTaken = -diff;

//                network.sendTo(new MessageHealthUpdate(current, max), (ServerPlayer) event.player);
            }
        }
    }

    public float getPlayerHealth() {
        if (System.currentTimeMillis() - lastUpdatePacketTime > CLIENT_MODE_DELAY) {
            Player clientPlayer = proxy.getClientPlayer();
            if (clientPlayer != null)
                return clientPlayer.getHealth();
        }

        return playerCurrentHealth;
    }

    public float getPlayerMaxHealth() {
        if (System.currentTimeMillis() - lastUpdatePacketTime > CLIENT_MODE_DELAY) {
            Player clientPlayer = proxy.getClientPlayer();
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
