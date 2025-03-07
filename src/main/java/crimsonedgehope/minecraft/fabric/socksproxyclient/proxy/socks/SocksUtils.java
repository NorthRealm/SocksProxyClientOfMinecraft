package crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.socks;

import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.ProxyEntry;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Environment(EnvType.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocksUtils {
    public static void apply(
            @NotNull ChannelPipeline pipeline,
            @NotNull List<ProxyEntry> entries
    ) {

        for (int i = entries.size() - 1; i >= 0; --i) {
            ProxyEntry entry = entries.get(i);
            switch (entry.getVersion()) {
                case SOCKS4 -> pipeline.addFirst("spc-socks4-" + i, new Socks4ProxyHandler(entry.getProxy().address(), entry.getSocksProxyCredential().getUsername()));
                case SOCKS5 -> pipeline.addFirst("spc-socks5-" + i, new Socks5ProxyHandler(entry.getProxy().address(), entry.getSocksProxyCredential().getUsername(), entry.getSocksProxyCredential().getPassword()));
            }
        }
    }
}
