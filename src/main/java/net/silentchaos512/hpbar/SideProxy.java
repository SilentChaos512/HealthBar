package net.silentchaos512.hpbar;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class SideProxy {
    @Nullable
    public Player getClientPlayer() {
        return null;
    }

    public static class Client extends SideProxy {
        @Override
        public Player getClientPlayer() {
            return Minecraft.getInstance().player;
        }
    }

    public static class Server extends SideProxy {
    }
}
