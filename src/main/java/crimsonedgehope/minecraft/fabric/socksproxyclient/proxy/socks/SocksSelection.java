package crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.socks;

import crimsonedgehope.minecraft.fabric.socksproxyclient.SocksProxyClient;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.GeneralConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.ServerConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.ProxyEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.IMixinClientConnection;
import io.netty.channel.ChannelPipeline;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocksSelection {
    private static final Logger LOGGER = SocksProxyClient.getLogger("Connect");

    public static void info(@NotNull List<ProxyEntry> proxies, @NotNull InetSocketAddress remote) {
        Objects.requireNonNull(remote.getHostString());
        if (proxies.isEmpty()) {
            LOGGER.info("[Direct] -> [Remote] {}:{}", remote.getHostString(), remote.getPort());
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (ProxyEntry entry : proxies) {
            builder.append(String.format("[%s] %s:%s -> ",
                    entry.getVersion().description,
                    ((InetSocketAddress) entry.getProxy().address()).getHostString(),
                    ((InetSocketAddress) entry.getProxy().address()).getPort()));
        }
        builder.append(String.format("[Remote] %s:%s", remote.getHostString(), remote.getPort()));
        LOGGER.info("{}", builder);
    }

    public static final Function<InetAddress, Supplier<List<ProxyEntry>>> supplierForMinecraft = (address) -> ServerConfig::getProxyEntryForMinecraft;

    public static final Supplier<List<ProxyEntry>> supplier = () -> GeneralConfig.getProxyEntry(true);

    public static void fire(@NotNull ClientConnection clientConnection, @NotNull ChannelPipeline pipeline) {
        IMixinClientConnection imixin = (IMixinClientConnection) clientConnection;

        ServerInfo serverInfo = imixin.socksProxyClient$getServerInfo();
        InetSocketAddress remote = imixin.socksProxyClient$getInetSocketAddress();

        // TODO: Remove
        if (Objects.nonNull(serverInfo) && serverInfo.getServerType().equals(ServerInfo.ServerType.OTHER)) {
            if (serverInfo.address.equals("mc.hypixel.net")) {
                apply(remote, List.of(), pipeline);
                return;
            }
        }

        InetAddress address = remote.getAddress();
        fire(remote, pipeline, supplierForMinecraft.apply(address));
    }

    public static void fire(InetSocketAddress remote, ChannelPipeline pipeline) {
        fire(remote, pipeline, supplier);
    }

    public static void fire(InetSocketAddress remote, ChannelPipeline pipeline, Supplier<List<ProxyEntry>> supplier) {
        if (Objects.isNull(remote)) {
            LOGGER.debug("fire: remote is null");
            return;
        }
        apply(remote, supplier.get(), pipeline);
    }

    private static void apply(@NotNull InetSocketAddress remote, @NotNull List<ProxyEntry> proxies, @NotNull ChannelPipeline pipeline) {
        SocksUtils.apply(pipeline, proxies);
        info(proxies, remote);
    }
}
