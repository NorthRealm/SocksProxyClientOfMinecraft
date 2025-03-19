package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.net.InetSocketAddress;

/**
 * This mixin is referred in {@link crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.SocksProxyClientMixinPlugin}.
 * Don't forget to check it there if renaming.
 */
@Mixin(MultiplayerServerListPinger.class)
@Environment(EnvType.CLIENT)
public class MixinMultiplayerServerListPinger0 {
    /**
     * @author SocksProxyClient
     * @reason Legacy ping part can't work properly without hacking Minecraft itself.
     */
    @Overwrite
    public void ping(InetSocketAddress socketAddress, final ServerAddress address, final ServerInfo serverInfo) {
        // NO-OP.
    }
}
