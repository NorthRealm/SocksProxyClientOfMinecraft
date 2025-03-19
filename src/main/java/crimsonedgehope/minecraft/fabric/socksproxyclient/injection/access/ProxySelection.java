package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access;

import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.socks.SocksSelection;
import io.netty.channel.ChannelPipeline;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Environment(EnvType.CLIENT)
public final class ProxySelection {

    public static void fire(@NotNull ClientConnection clientConnection, @NotNull ChannelPipeline pipeline) {
        ServerInfo serverInfo = ((IMixinClientConnection) clientConnection).socksProxyClient$getServerInfo();
        InetSocketAddress remote = ((IMixinClientConnection) clientConnection).socksProxyClient$getInetSocketAddress();

        if (Objects.nonNull(serverInfo)) {
            fire(serverInfo, remote, pipeline);
            return;
        }

        if (((IMixinClientConnection) clientConnection).socksProxyClient$isPingingUseProxy()) {
            fireApply(remote, pipeline);
        } else {
            fireNoApply(remote, pipeline);
        }
    }

    public static void fire(@NotNull ServerInfo serverInfo, @NotNull InetSocketAddress remote, @NotNull ChannelPipeline pipeline) {
        if (serverInfo.getServerType().equals(ServerInfo.ServerType.OTHER) && !((IMixinServerInfo) serverInfo).socksProxyClient$isUseProxy()) {
            fireNoApply(remote, pipeline);
        } else {
            fireApply(remote, pipeline);
        }
    }

    private static void fireNoApply(@NotNull InetSocketAddress remote, @NotNull ChannelPipeline pipeline) {
        SocksSelection.fire(remote, pipeline, List::of);
    }

    private static void fireApply(@NotNull InetSocketAddress remote, @NotNull ChannelPipeline pipeline) {
        SocksSelection.fire(remote, pipeline, SocksSelection.supplierForMinecraft.apply(remote.getAddress()));
    }
}
