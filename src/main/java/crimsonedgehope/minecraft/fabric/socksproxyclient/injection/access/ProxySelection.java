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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Environment(EnvType.CLIENT)
public final class ProxySelection {

    public static void fire(@NotNull ClientConnection clientConnection, @NotNull ChannelPipeline pipeline) {
        IMixinClientConnection imixin = (IMixinClientConnection) clientConnection;

        ServerInfo serverInfo = imixin.socksProxyClient$getServerInfo();
        InetSocketAddress remote = imixin.socksProxyClient$getInetSocketAddress();

        // TODO: Remove
        if (Objects.nonNull(serverInfo) && serverInfo.getServerType().equals(ServerInfo.ServerType.OTHER)) {
            if (serverInfo.address.equals("mc.hypixel.net")) {
                SocksSelection.fire(remote, pipeline, List::of);
                return;
            }
        }

        InetAddress address = remote.getAddress();
        SocksSelection.fire(remote, pipeline, SocksSelection.supplierForMinecraft.apply(address));
    }
}
