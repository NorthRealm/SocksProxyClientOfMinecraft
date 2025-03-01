package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.net.InetSocketAddress;

@Mixin(MultiplayerServerListPinger.class)
@Environment(EnvType.CLIENT)
public class MixinMultiplayerServerListPinger {
    @Overwrite
    public void ping(InetSocketAddress socketAddress, final ServerAddress address, final ServerInfo serverInfo) {

    }
}
