package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.network;

import crimsonedgehope.minecraft.fabric.socksproxyclient.SocksProxyClient;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.IMixinClientConnection;
import io.netty.channel.ChannelFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetSocketAddress;

@Environment(EnvType.CLIENT)
@Mixin(ClientConnection.class)
public class MixinClientConnection implements IMixinClientConnection {
    @Unique
    private InetSocketAddress remote;
    @Unique
    private ServerInfo serverInfo;

    @Override
    public InetSocketAddress socksProxyClient$getInetSocketAddress() {
        return remote;
    }

    @Override
    public void socksProxyClient$setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.remote = inetSocketAddress;
    }

    @Override
    public ServerInfo socksProxyClient$getServerInfo() {
        return serverInfo;
    }

    @Override
    public void socksProxyClient$setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    @Inject(
            method = "connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/network/ClientConnection;)Lio/netty/channel/ChannelFuture;",
            at = @At("HEAD")
    )
    private static void injected(InetSocketAddress address, boolean useEpoll, ClientConnection connection, CallbackInfoReturnable<ChannelFuture> cir) {
        ((IMixinClientConnection) connection).socksProxyClient$setInetSocketAddress(address);
        SocksProxyClient.getLogger("Connect").debug("Connecting to remote Minecraft server {}", address);
    }
}
