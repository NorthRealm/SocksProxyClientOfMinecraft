package crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.socks;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.SocksProxyClientConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.ProxyEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.http.HttpProxyUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private static final MultiplayerServerListPinger pinger = new MultiplayerServerListPinger();
    private static Long testTime = 0L;

    static {
        scheduler.scheduleAtFixedRate(pinger::tick, 0, 50L, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scheduler.shutdownNow();
            pinger.cancel();
        }));
    }

    public static void test() {
        if (System.currentTimeMillis() - testTime <= 5000L) {
            SocksProxyClientConfig.LOGGER.warn("NO TEST SPAMMING");
            return;
        }
        testTime = System.currentTimeMillis();
        for (String url : new String[]{
                "https://api.mojang.com",
                "https://ipinfo.io",
                "http://connectivitycheck.gstatic.com/generate_204"
        }) {
            scheduler.submit(() -> {
                testHTTP(url);
            });
        }
        for (String domain : new String[]{
            "mc.hypixel.net",
            "play.cubecraft.net"
        }) {
            scheduler.submit(() -> {
                testMinecraftPing(domain);
            });
        }
    }

    public static void testMinecraftPing(final String target) {
        try {
            showTestStart(target);
            ServerInfo entry = new ServerInfo(target, target, ServerInfo.ServerType.OTHER);
            pinger.add(entry, () -> {}, () -> {
                showTestResult(new Pair<>(true, null), target);
                SocksProxyClientConfig.LOGGER.info("Pinged {}: Ping {}ms\n Version: {}\n Protocol version: {}\n Player count: {}",
                        target, entry.ping, entry.version.getLiteralString(), entry.protocolVersion, entry.playerCountLabel.getString());
            });
        } catch (Exception e) {
            showTestResult(new Pair<>(false, new RuntimeException("Failed to ping!", e)), target);
        }
    }

    public static void testHTTP(final String target) {
        final CompletableFuture<Pair<Boolean, Throwable>> test = CompletableFuture.supplyAsync(() -> {
            try {
                URL url = URI.create(target).toURL();
                final Proxy httpProxy = HttpProxyUtils.getProxyObject(true);
                if (httpProxy.equals(Proxy.NO_PROXY)) {
                    SocksProxyClientConfig.LOGGER.warn("No proxy to test.");
                    return new Pair<>(true, null);
                }

                final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(httpProxy);
                urlConnection.setConnectTimeout(com.mojang.authlib.minecraft.client.MinecraftClient.CONNECT_TIMEOUT_MS);
                urlConnection.setReadTimeout(com.mojang.authlib.minecraft.client.MinecraftClient.READ_TIMEOUT_MS);
                showTestStart(target);
                int res = urlConnection.getResponseCode();

                if (res != -1) {
                    if (res == HttpStatus.SC_OK || res == HttpStatus.SC_NO_CONTENT) {
                        SocksProxyClientConfig.LOGGER.info("{} responded with {}", target, res);
                    } else {
                        SocksProxyClientConfig.LOGGER.warn("{} responded with {}", target, res);
                    }
                    if (res == HttpStatus.SC_OK) {
                        final InputStream inputStream = urlConnection.getInputStream();
                        final String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                        final Gson gson = new Gson();
                        final JsonObject jsonObject = gson.fromJson(result, JsonObject.class);
                        SocksProxyClientConfig.LOGGER.info("{} response: {}", target, jsonObject.toString());
                    }

                    urlConnection.disconnect();
                } else {
                    SocksProxyClientConfig.LOGGER.warn("{} is not responding.", target);
                }
            } catch (JsonSyntaxException e) {
                return new Pair<>(true, new RuntimeException(target + " sent back no json.", e));
            } catch (IOException e) {
                return new Pair<>(false, new RuntimeException("IO failure!!", e));
            }
            return new Pair<>(true, null);
        });
        showTestResult(test, target);
    }

    private static void showTestStart(final String target) {
        SocksProxyClientConfig.LOGGER.info("Testing connection to {}", target);
        MinecraftClient.getInstance().submit(() -> {
            SystemToast.show(
                    MinecraftClient.getInstance().getToastManager(),
                    new SystemToast.Type(1000L),
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_TESTING),
                    Text.literal(target));
        });
    }

    private static void showTestResult(Pair<Boolean, Throwable> res, final String target) {
        MinecraftClient.getInstance().submit(() -> {
            SystemToast.add(
                    MinecraftClient.getInstance().getToastManager(),
                    new SystemToast.Type(),
                    Text.translatable(res.getLeft()
                            ? TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_TEST_SUCCESS
                            : TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_TEST_FAILURE
                    ),
                    Text.literal(target));
        });

        if (res.getLeft()) {
            return;
        }

        Throwable t = res.getRight();
        SocksProxyClientConfig.LOGGER.error("", t);
        if (Objects.nonNull(t) && !(t instanceof JsonSyntaxException)) {
            SocksProxyClientConfig.LOGGER.error("Test not successful.", t);
        }
    }

    private static void showTestResult(final CompletableFuture<Pair<Boolean, Throwable>> test, final String target) {
        test.thenApplyAsync(v -> {
            showTestResult(v, target);
            return null;
        });
    }
}
