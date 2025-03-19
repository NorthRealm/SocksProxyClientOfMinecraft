package crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.ConfigUtils;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.GeneralConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.SocksProxyClientConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.ProxyEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.SocksProxyClientConfigEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.controller.ProxyEntryControllerBuilder;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.controller.ValidStringControllerBuilder;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.screen.ProxyEntryEditScreen;
import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.IMixinServerInfo;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.http.HttpProxyUtils;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

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

final class GeneralCategory extends YACLCategory<GeneralConfig> {

    SocksProxyClientConfigEntry<Boolean> useProxy;
    SocksProxyClientConfigEntry<List> proxies;
    SocksProxyClientConfigEntry<List> httpTestSubjects;
    SocksProxyClientConfigEntry<List> minecraftTestSubjects;

    GeneralCategory(YACLAccess yacl) {
        super(yacl, GeneralConfig.class);
    }

    @Override
    public ConfigCategory buildConfigCategory() throws Exception {
        ConfigCategory.Builder categoryBuilder = ConfigCategory.createBuilder();

        categoryBuilder.name(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL))
                .tooltip(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_TOOLTIP));

        useProxy = entryField("useProxy", Boolean.class);
        Option<Boolean> yaclUseProxy = Option.<Boolean>createBuilder()
                .name(useProxy.getEntryTranslateKey())
                .binding(useProxy.getDefaultValue(), useProxy::getValue, useProxy::setValue)
                .controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter().coloured(true))
                .flag(OptionFlag.GAME_RESTART)
                .build();
        categoryBuilder.option(yaclUseProxy);

        proxies = entryField("proxies", List.class);
        ListOption<ProxyEntry> yaclProxies = ListOption.<ProxyEntry>createBuilder()
                .name(proxies.getEntryTranslateKey())
                .description(OptionDescription.of(proxies.getDescriptionTranslateKey()))
                .initial((ProxyEntry) proxies.getDefaultValue().get(0))
                .binding((List<ProxyEntry>) proxies.getDefaultValue(), proxies::getValue, proxies::setValue)
                .collapsed(false)
                .controller(opt -> ProxyEntryControllerBuilder.create((Option<ProxyEntry>) opt).action((screen, entry, callback) -> {
                    MinecraftClient.getInstance().setScreen(new ProxyEntryEditScreen(screen, entry, callback));
                }))
                .insertEntriesAtEnd(true)
                .flag(OptionFlag.GAME_RESTART)
                .available(useProxy.getValue())
                .build();

        categoryBuilder.group(yaclProxies);

        OptionGroup.Builder proxyGroupBuilder = OptionGroup.createBuilder()
                .name(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY));

        ButtonOption yaclTestReachability = ButtonOption.createBuilder()
                .name(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_TEST))
                .description(OptionDescription.of(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_TEST_TOOLTIP)))
                .available(true)
                .action((screen, opt) -> testProxy())
                .available(useProxy.getValue())
                .build();

        httpTestSubjects = entryField("httpTestSubjects", List.class);
        ListOption<String> yaclHTTPTestSubjects = ListOption.<String>createBuilder()
                .name(httpTestSubjects.getEntryTranslateKey())
                .description(OptionDescription.of(httpTestSubjects.getDescriptionTranslateKey()))
                .initial("")
                .binding((List<String>) httpTestSubjects.getDefaultValue(), httpTestSubjects::getValue, httpTestSubjects::setValue)
                .collapsed(false)
                .controller(opt -> StringControllerBuilder.create((Option<String>) opt))
                .insertEntriesAtEnd(true)
                .available(useProxy.getValue())
                .build();

        minecraftTestSubjects = entryField("minecraftTestSubjects", List.class);
        ListOption<String> yaclMinecraftTestSubjects = ListOption.<String>createBuilder()
                .name(minecraftTestSubjects.getEntryTranslateKey())
                .description(OptionDescription.of(minecraftTestSubjects.getDescriptionTranslateKey()))
                .initial("")
                .binding((List<String>) minecraftTestSubjects.getDefaultValue(), minecraftTestSubjects::getValue, minecraftTestSubjects::setValue)
                .collapsed(false)
                .controller(opt -> ValidStringControllerBuilder.create((Option<String>) opt).validityPredication(ConfigUtils.minecraftAddressValidity))
                .insertEntriesAtEnd(true)
                .available(useProxy.getValue())
                .build();

        proxyGroupBuilder.option(yaclTestReachability);

        yaclUseProxy.addListener((opt, v) -> {
            yaclProxies.setAvailable(v);
            yaclTestReachability.setAvailable(v);
            yaclHTTPTestSubjects.setAvailable(v);
            yaclMinecraftTestSubjects.setAvailable(v);
        });

        categoryBuilder.group(proxyGroupBuilder.build());

        categoryBuilder.group(yaclHTTPTestSubjects);
        categoryBuilder.group(yaclMinecraftTestSubjects);

        return categoryBuilder.build();
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

    private static void testProxy() {
        if (System.currentTimeMillis() - testTime <= 5000L) {
            SocksProxyClientConfig.LOGGER.warn("NO TEST SPAMMING");
            return;
        }
        testTime = System.currentTimeMillis();
        for (String url : GeneralConfig.getHTTPTestSubjects()) {
            scheduler.submit(() -> {
                testHTTP(url);
            });
        }
        for (String domain : GeneralConfig.getMinecraftTestSubjects()) {
            scheduler.submit(() -> {
                testMinecraftPing(domain);
            });
        }
    }

    private static void testMinecraftPing(final String target) {
        try {
            showTestStart(target);
            ServerInfo entry = new ServerInfo(target, target, ServerInfo.ServerType.OTHER);
            ((IMixinServerInfo) entry).socksProxyClient$setUseProxy(true);
            pinger.add(entry, () -> {}, () -> {
                showTestResult(new Pair<>(true, null), target);
                SocksProxyClientConfig.LOGGER.info("Pinged {}: Ping {}ms\n Version: {}\n Protocol version: {}\n Player count: {}",
                        target, entry.ping, entry.version.getLiteralString(), entry.protocolVersion, entry.playerCountLabel.getString());
            });
        } catch (Exception e) {
            showTestResult(new Pair<>(false, new RuntimeException("Failed to ping!", e)), target);
        }
    }

    private static void testHTTP(final String target) {
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
