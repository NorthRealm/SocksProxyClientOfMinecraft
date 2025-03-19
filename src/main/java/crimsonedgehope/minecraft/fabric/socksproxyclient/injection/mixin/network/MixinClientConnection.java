package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.network;

import com.llamalad7.mixinextras.sugar.Local;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.IMixinClientConnection;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.IMixinMultiValueDebugSampleLogImpl;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.socks.SocksSelection;
import io.netty.channel.ChannelFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
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
    private boolean pingingUseProxy = true;

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
    public boolean socksProxyClient$isPingingUseProxy() {
        return pingingUseProxy;
    }

    @Override
    public void socksProxyClient$setPingingUseProxy(boolean useProxy) {
        this.pingingUseProxy = useProxy;
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
            method = "connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/util/profiler/MultiValueDebugSampleLogImpl;)Lnet/minecraft/network/ClientConnection;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;resetPacketSizeLog(Lnet/minecraft/util/profiler/MultiValueDebugSampleLogImpl;)V", shift = At.Shift.AFTER)
    )
    private static void injected(InetSocketAddress address, boolean useEpoll, MultiValueDebugSampleLogImpl packetSizeLog, CallbackInfoReturnable<ClientConnection> cir, @Local ClientConnection connection) {
        ((IMixinClientConnection) connection).socksProxyClient$setPingingUseProxy(((IMixinMultiValueDebugSampleLogImpl) packetSizeLog).socksProxyClient$isUseProxy());
        SocksSelection.LOGGER.debug("Pinging to remote Minecraft server {}", address);
    }

    @Inject(
            method = "connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/network/ClientConnection;)Lio/netty/channel/ChannelFuture;",
            at = @At("HEAD")
    )
    private static void injected(InetSocketAddress address, boolean useEpoll, ClientConnection connection, CallbackInfoReturnable<ChannelFuture> cir) {
        ((IMixinClientConnection) connection).socksProxyClient$setInetSocketAddress(address);
        SocksSelection.LOGGER.debug("Connecting to remote Minecraft server {}", address);
    }
}
