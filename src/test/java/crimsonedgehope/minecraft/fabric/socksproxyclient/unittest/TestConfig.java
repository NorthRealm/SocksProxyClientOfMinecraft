package crimsonedgehope.minecraft.fabric.socksproxyclient.unittest;

import crimsonedgehope.minecraft.fabric.socksproxyclient.config.ConfigUtils;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.GeneralConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.MiscellaneousConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.ServerConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.ProxyEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.SocksProxyClientConfigEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.doh.DOHProvider;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.socks.SocksVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

class TestConfig {

    @BeforeAll
    static void prepare() {
        Utils.preBootstrap();
    }

    GeneralConfig generalConfig;
    ServerConfig serverConfig;
    MiscellaneousConfig miscellaneousConfig;

    final Runnable deleteFiles = () -> {
        Assertions.assertDoesNotThrow(() -> {
            generalConfig.getConfigFile().delete();
            serverConfig.getConfigFile().delete();
            miscellaneousConfig.getConfigFile().delete();
        });
    };

    final Runnable getObjects = () -> {
        Assertions.assertDoesNotThrow(() -> {
            generalConfig = ConfigUtils.getConfigInstance(GeneralConfig.class);
            serverConfig = ConfigUtils.getConfigInstance(ServerConfig.class);
            miscellaneousConfig = ConfigUtils.getConfigInstance(MiscellaneousConfig.class);
        });

        Assertions.assertNotNull(generalConfig.getConfigFile());
        Assertions.assertNotNull(serverConfig.getConfigFile());
        Assertions.assertNotNull(miscellaneousConfig.getConfigFile());
    };

    final Runnable load = () -> {
        Assertions.assertDoesNotThrow(ConfigUtils::loadAllConfig);
    };

    final Runnable save = () -> {
        Assertions.assertDoesNotThrow(ConfigUtils::saveAllConfig);
    };

    final Runnable reload = () -> {
        save.run();
        load.run();
    };

    @FunctionalInterface
    interface ActionOnEntry {
        <T> void apply(SocksProxyClientConfigEntry<T> entry, T value);
    }

    ActionOnEntry setEntry = SocksProxyClientConfigEntry::setValue;

    ActionOnEntry compareValue = new ActionOnEntry() {
        @Override
        public <T> void apply(SocksProxyClientConfigEntry<T> entry, T expected) {
            Assertions.assertEquals(expected, entry.getValue());
        }
    };

    @Test
    @DisplayName("Test config file loading and writing")
    void testConfigInitialization() {
        load.run();
        getObjects.run();
        deleteFiles.run();
        reload.run();
    }

    @Test
    @DisplayName("Test general config manipulation")
    void testGeneralConfig() {
        reload.run();
        getObjects.run();
        Assertions.assertDoesNotThrow(() -> {
            SocksProxyClientConfigEntry<Boolean> useProxy = generalConfig.getEntryField("useProxy", Boolean.class);
            setEntry.apply(useProxy, !useProxy.getDefaultValue());

            SocksProxyClientConfigEntry<List> proxies = generalConfig.getEntryField("proxies", List.class);
            setEntry.apply(proxies, new ArrayList<String>());
        });

        reload.run();
        getObjects.run();
        Assertions.assertDoesNotThrow(() -> {
            SocksProxyClientConfigEntry<Boolean> useProxy = generalConfig.getEntryField("useProxy", Boolean.class);
            compareValue.apply(useProxy, !useProxy.getDefaultValue());

            SocksProxyClientConfigEntry<List> proxies = generalConfig.getEntryField("proxies", List.class);
            Assertions.assertTrue(proxies.getValue().isEmpty());
        });

        final List<ProxyEntry> fakeProxies = new ArrayList<>() {{
            add(new ProxyEntry(SocksVersion.SOCKS5, new InetSocketAddress("localhost", 1081)));
            add(new ProxyEntry(SocksVersion.SOCKS4, new InetSocketAddress("10.0.0.1", 1082)));
            add(new ProxyEntry(SocksVersion.SOCKS4, new InetSocketAddress("172.16.0.1", 1083)));
            add(new ProxyEntry(SocksVersion.SOCKS5, new InetSocketAddress("192.168.0.1", 1084), "admin", "123456"));
        }};

        reload.run();
        getObjects.run();
        Assertions.assertDoesNotThrow(() -> {
            SocksProxyClientConfigEntry<List> proxies = generalConfig.getEntryField("proxies", List.class);
            setEntry.apply(proxies, fakeProxies);
        });

        reload.run();
        getObjects.run();
        Assertions.assertDoesNotThrow(() -> {
            SocksProxyClientConfigEntry<List> proxies = generalConfig.getEntryField("proxies", List.class);
            Assertions.assertFalse(proxies.getValue().isEmpty());

            Assertions.assertEquals(SocksVersion.SOCKS5, ((ProxyEntry) proxies.getValue().get(0)).getVersion());
            Assertions.assertEquals(SocksVersion.SOCKS4, ((ProxyEntry) proxies.getValue().get(1)).getVersion());
            Assertions.assertEquals(SocksVersion.SOCKS4, ((ProxyEntry) proxies.getValue().get(2)).getVersion());
            Assertions.assertEquals(SocksVersion.SOCKS5, ((ProxyEntry) proxies.getValue().get(3)).getVersion());

            Assertions.assertEquals("localhost", ((InetSocketAddress) ((ProxyEntry) proxies.getValue().get(0)).getProxy().address()).getHostString());
            Assertions.assertEquals(1081, ((InetSocketAddress) ((ProxyEntry) proxies.getValue().get(0)).getProxy().address()).getPort());

            Assertions.assertEquals("10.0.0.1", ((InetSocketAddress) ((ProxyEntry) proxies.getValue().get(1)).getProxy().address()).getHostString());
            Assertions.assertEquals(1082, ((InetSocketAddress) ((ProxyEntry) proxies.getValue().get(1)).getProxy().address()).getPort());

            Assertions.assertEquals("172.16.0.1", ((InetSocketAddress) ((ProxyEntry) proxies.getValue().get(2)).getProxy().address()).getHostString());
            Assertions.assertEquals(1083, ((InetSocketAddress) ((ProxyEntry) proxies.getValue().get(2)).getProxy().address()).getPort());

            Assertions.assertEquals("192.168.0.1", ((InetSocketAddress) ((ProxyEntry) proxies.getValue().get(3)).getProxy().address()).getHostString());
            Assertions.assertEquals(1084, ((InetSocketAddress) ((ProxyEntry) proxies.getValue().get(3)).getProxy().address()).getPort());
            Assertions.assertEquals("admin", ((ProxyEntry) proxies.getValue().get(3)).getSocksProxyCredential().getUsername());
            Assertions.assertEquals("123456", ((ProxyEntry) proxies.getValue().get(3)).getSocksProxyCredential().getPassword());
        });
    }

    @Test
    @DisplayName("Test miscellaneous config manipulation")
    void testMiscellaneousConfig() {
        for (ActionOnEntry action : new ActionOnEntry[] {setEntry, compareValue}) {
            reload.run();
            getObjects.run();
            Assertions.assertDoesNotThrow(() -> {
                SocksProxyClientConfigEntry<Boolean> buttonsInMultiplayerScreen = miscellaneousConfig.getEntryField("buttonsInMultiplayerScreen", Boolean.class);
                action.apply(buttonsInMultiplayerScreen, !buttonsInMultiplayerScreen.getDefaultValue());

                SocksProxyClientConfigEntry<Boolean> checkUpdates = miscellaneousConfig.getEntryField("checkUpdates", Boolean.class);
                action.apply(checkUpdates, !checkUpdates.getDefaultValue());
            });
        }
    }

    @Test
    @DisplayName("Test server config manipulation")
    void testServerConfig() {
        for (ActionOnEntry action : new ActionOnEntry[] {setEntry, compareValue}) {
            Assertions.assertDoesNotThrow(() -> {
                reload.run();
                getObjects.run();

                SocksProxyClientConfigEntry<Boolean> proxyMinecraft = serverConfig.getEntryField("proxyMinecraft", Boolean.class);
                action.apply(proxyMinecraft, !proxyMinecraft.getDefaultValue());

                SocksProxyClientConfigEntry<Boolean> minecraftDomainNameResolutionUseProxy = serverConfig.getEntryField("minecraftDomainNameResolutionUseProxy", Boolean.class);
                action.apply(minecraftDomainNameResolutionUseProxy, !minecraftDomainNameResolutionUseProxy.getDefaultValue());

                SocksProxyClientConfigEntry<Boolean> minecraftDomainNameResolutionDismissSystemHosts = serverConfig.getEntryField("minecraftDomainNameResolutionDismissSystemHosts", Boolean.class);
                action.apply(minecraftDomainNameResolutionDismissSystemHosts, !minecraftDomainNameResolutionDismissSystemHosts.getDefaultValue());

                SocksProxyClientConfigEntry<String> minecraftDomainNameResolutionDohProviderUrl = serverConfig.getEntryField("minecraftDomainNameResolutionDohProviderUrl", String.class);
                action.apply(minecraftDomainNameResolutionDohProviderUrl, "https://example.org/dns-query");

                SocksProxyClientConfigEntry<Boolean> proxyYggdrasil = serverConfig.getEntryField("proxyYggdrasil", Boolean.class);
                action.apply(proxyYggdrasil, !proxyYggdrasil.getDefaultValue());

                SocksProxyClientConfigEntry<Boolean> proxyRealmsApi = serverConfig.getEntryField("proxyRealmsApi", Boolean.class);
                action.apply(proxyRealmsApi, !proxyRealmsApi.getDefaultValue());

                SocksProxyClientConfigEntry<Boolean> proxyPlayerSkinDownload = serverConfig.getEntryField("proxyPlayerSkinDownload", Boolean.class);
                action.apply(proxyPlayerSkinDownload, !proxyPlayerSkinDownload.getDefaultValue());

                SocksProxyClientConfigEntry<Boolean> proxyServerResourceDownload = serverConfig.getEntryField("proxyServerResourceDownload", Boolean.class);
                action.apply(proxyServerResourceDownload, !proxyServerResourceDownload.getDefaultValue());

                SocksProxyClientConfigEntry<Boolean> proxyBlockListSupplier = serverConfig.getEntryField("proxyBlockListSupplier", Boolean.class);
                action.apply(proxyBlockListSupplier, !proxyBlockListSupplier.getDefaultValue());

                SocksProxyClientConfigEntry<Boolean> httpRemoteResolve = serverConfig.getEntryField("httpRemoteResolve", Boolean.class);
                action.apply(httpRemoteResolve, !httpRemoteResolve.getDefaultValue());

                SocksProxyClientConfigEntry<Boolean> imposeProxyOnMinecraftLoopback = serverConfig.getEntryField("imposeProxyOnMinecraftLoopback", Boolean.class);
                action.apply(imposeProxyOnMinecraftLoopback, !imposeProxyOnMinecraftLoopback.getDefaultValue());

                SocksProxyClientConfigEntry<DOHProvider> minecraftDomainNameResolutionDohProvider = serverConfig.getEntryField("minecraftDomainNameResolutionDohProvider", DOHProvider.class);
                action.apply(minecraftDomainNameResolutionDohProvider, DOHProvider.CUSTOM);
            });
        }
    }
}
