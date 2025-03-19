package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerInfo;

import java.net.InetSocketAddress;

@Environment(EnvType.CLIENT)
public interface IMixinClientConnection {
    /** For both Ping and Join */
    void socksProxyClient$setInetSocketAddress(InetSocketAddress inetSocketAddress);
    /** For both Ping and Join */
    InetSocketAddress socksProxyClient$getInetSocketAddress();

    /** For pinging a server */
    void socksProxyClient$setPingingUseProxy(boolean useProxy);
    /** For pinging a server */
    boolean socksProxyClient$isPingingUseProxy();

    /** For joining a server */
    void socksProxyClient$setServerInfo(ServerInfo serverInfo);
    /** For joining a server */
    ServerInfo socksProxyClient$getServerInfo();
}
