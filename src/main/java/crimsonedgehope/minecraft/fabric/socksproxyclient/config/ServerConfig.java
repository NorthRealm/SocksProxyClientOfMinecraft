package crimsonedgehope.minecraft.fabric.socksproxyclient.config;

import com.google.gson.JsonObject;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.ProxyEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.SocksProxyClientConfigEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.doh.DOHProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class ServerConfig extends SocksProxyClientConfig {

    private static final ServerConfig INSTANCE;

    static {
        INSTANCE = new ServerConfig();
    }

    private static final SocksProxyClientConfigEntry<Boolean> proxyMinecraft =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "proxyMinecraft",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_PROXYMINECRAFT),
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_PROXYMINECRAFT_TOOLTIP),
                    true);
    private static final SocksProxyClientConfigEntry<Boolean> minecraftDomainNameResolutionUseProxy =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "minecraftDomainNameResolutionUseProxy",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_MINECRAFTDOMAINNAMERESOLUTION_USEPROXY),
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_MINECRAFTDOMAINNAMERESOLUTION_USEPROXY_TOOLTIP),
                    false);
    private static final SocksProxyClientConfigEntry<Boolean> minecraftDomainNameResolutionDismissSystemHosts =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "minecraftDomainNameResolutionDismissSystemHosts",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_MINECRAFTDOMAINNAMERESOLUTION_DISMISSSYSTEMHOSTS),
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_MINECRAFTDOMAINNAMERESOLUTION_DISMISSSYSTEMHOSTS_TOOLTIP),
                    true);
    private static final SocksProxyClientConfigEntry<DOHProvider> minecraftDomainNameResolutionDohProvider =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "minecraftDomainNameResolutionDohProvider",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_MINECRAFTDOMAINNAMERESOLUTION_DOHPROVIDER),
                    DOHProvider.CLOUDFLARE);
    private static final SocksProxyClientConfigEntry<String> minecraftDomainNameResolutionDohProviderUrl =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "minecraftDomainNameResolutionDohProviderUrl",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_MINECRAFTDOMAINNAMERESOLUTION_DOHPROVIDERURL),
                    DOHProvider.CLOUDFLARE.url);
    private static final SocksProxyClientConfigEntry<Boolean> proxyYggdrasil =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "proxyYggdrasil",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_SERVICES_PROXYYGGDRASIL),
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_SERVICES_PROXYYGGDRASIL_TOOLTIP),
                    true);
    private static final SocksProxyClientConfigEntry<Boolean> proxyRealmsApi =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "proxyRealmsApi",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_SERVICES_PROXYREALMSAPI),
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_SERVICES_PROXYREALMSAPI_TOOLTIP),
                    true);
    private static final SocksProxyClientConfigEntry<Boolean> proxyPlayerSkinDownload =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "proxyPlayerSkinDownload",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_SERVICES_PROXYPLAYERSKINDOWNLOAD),
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_SERVICES_PROXYPLAYERSKINDOWNLOAD_TOOLTIP),
                    true);
    private static final SocksProxyClientConfigEntry<Boolean> proxyServerResourceDownload =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "proxyServerResourceDownload",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_SERVICES_PROXYSERVERRESOURCEDOWNLOAD),
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_SERVICES_PROXYSERVERRESOURCEDOWNLOAD_TOOLTIP),
                    true);
    private static final SocksProxyClientConfigEntry<Boolean> proxyBlockListSupplier =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "proxyBlockListSupplier",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_SERVICES_PROXYBLOCKLISTSUPPLIER),
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_SERVICES_PROXYBLOCKLISTSUPPLIER_TOOLTIP),
                    true);
    private static final SocksProxyClientConfigEntry<Boolean> httpRemoteResolve =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "httpRemoteResolve",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_SERVICES_HTTPREMOTERESOLVE),
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_SERVICES_HTTPREMOTERESOLVE_TOOLTIP),
                    true);

    public static final String CATEGORY = "server";

    private ServerConfig() {
        super(CATEGORY + ".json");
    }

    public static boolean usingProxyOnMinecraft() {
        return GeneralConfig.usingProxy() && proxyMinecraft.getValue();
    }

    public static List<ProxyEntry> getProxyEntryForMinecraft() {
        return getProxyEntryForMinecraft(usingProxyOnMinecraft());
    }

    public static List<ProxyEntry> getProxyEntryForMinecraft(boolean useProxy) {
        return GeneralConfig.getProxyEntry(useProxy);
    }

    public static boolean shouldProxyYggdrasil() {
        return GeneralConfig.usingProxy() && proxyYggdrasil.getValue();
    }

    public static boolean shouldProxyRealmsApi() {
        return GeneralConfig.usingProxy() && proxyRealmsApi.getValue();
    }

    public static boolean shouldProxyPlayerSkinDownload() {
        return GeneralConfig.usingProxy() && proxyPlayerSkinDownload.getValue();
    }

    public static boolean shouldProxyServerResourceDownload() {
        return GeneralConfig.usingProxy() && proxyServerResourceDownload.getValue();
    }

    public static boolean shouldProxyBlockListSupplier() {
        return GeneralConfig.usingProxy() && proxyBlockListSupplier.getValue();
    }

    public static boolean httpRemoteResolve() {
        return GeneralConfig.usingProxy() && httpRemoteResolve.getValue();
    }

    public static boolean minecraftRemoteResolve() {
        return usingProxyOnMinecraft() && minecraftDomainNameResolutionUseProxy.getValue();
    }

    public static boolean minecraftRemoteResolveDismissSystemHosts() {
        return minecraftDomainNameResolutionDismissSystemHosts.getValue();
    }

    public static String minecraftRemoteResolveProviderUrl() {
        if (!minecraftRemoteResolve()) {
            return null;
        }
        if (minecraftDomainNameResolutionDohProvider.getValue().equals(DOHProvider.CUSTOM)) {
            return minecraftDomainNameResolutionDohProviderUrl.getValue();
        }
        return minecraftDomainNameResolutionDohProvider.getValue().url;
    }

    @Override
    public JsonObject defaultEntries() {
        JsonObject obj = new JsonObject();
        obj.addProperty(proxyMinecraft.getJsonEntry(), proxyMinecraft.getDefaultValue());
        obj.addProperty(minecraftDomainNameResolutionUseProxy.getJsonEntry(), minecraftDomainNameResolutionUseProxy.getDefaultValue());
        obj.addProperty(minecraftDomainNameResolutionDismissSystemHosts.getJsonEntry(), minecraftDomainNameResolutionDismissSystemHosts.getDefaultValue());
        obj.addProperty(minecraftDomainNameResolutionDohProvider.getJsonEntry(), minecraftDomainNameResolutionDohProvider.getDefaultValue().name());
        obj.addProperty(minecraftDomainNameResolutionDohProviderUrl.getJsonEntry(), minecraftDomainNameResolutionDohProviderUrl.getDefaultValue());
        obj.addProperty(proxyYggdrasil.getJsonEntry(), proxyYggdrasil.getDefaultValue());
        obj.addProperty(proxyRealmsApi.getJsonEntry(), proxyRealmsApi.getDefaultValue());
        obj.addProperty(proxyPlayerSkinDownload.getJsonEntry(), proxyPlayerSkinDownload.getDefaultValue());
        obj.addProperty(proxyServerResourceDownload.getJsonEntry(), proxyServerResourceDownload.getDefaultValue());
        obj.addProperty(proxyBlockListSupplier.getJsonEntry(), proxyBlockListSupplier.getDefaultValue());
        obj.addProperty(httpRemoteResolve.getJsonEntry(), httpRemoteResolve.getDefaultValue());
        return obj;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty(proxyMinecraft.getJsonEntry(), proxyMinecraft.getValue());
        obj.addProperty(minecraftDomainNameResolutionUseProxy.getJsonEntry(), minecraftDomainNameResolutionUseProxy.getValue());
        obj.addProperty(minecraftDomainNameResolutionDismissSystemHosts.getJsonEntry(), minecraftDomainNameResolutionDismissSystemHosts.getValue());
        obj.addProperty(minecraftDomainNameResolutionDohProvider.getJsonEntry(), minecraftDomainNameResolutionDohProvider.getValue().name());
        obj.addProperty(minecraftDomainNameResolutionDohProviderUrl.getJsonEntry(), minecraftDomainNameResolutionDohProviderUrl.getValue());
        obj.addProperty(proxyYggdrasil.getJsonEntry(), proxyYggdrasil.getValue());
        obj.addProperty(proxyRealmsApi.getJsonEntry(), proxyRealmsApi.getValue());
        obj.addProperty(proxyPlayerSkinDownload.getJsonEntry(), proxyPlayerSkinDownload.getValue());
        obj.addProperty(proxyServerResourceDownload.getJsonEntry(), proxyServerResourceDownload.getValue());
        obj.addProperty(proxyBlockListSupplier.getJsonEntry(), proxyBlockListSupplier.getValue());
        obj.addProperty(httpRemoteResolve.getJsonEntry(), httpRemoteResolve.getValue());
        return obj;
    }

    @Override
    public void fromJsonObject(JsonObject object) {
        proxyMinecraft.setValue(object.get(proxyMinecraft.getJsonEntry()).getAsBoolean());
        minecraftDomainNameResolutionUseProxy.setValue(object.get(minecraftDomainNameResolutionUseProxy.getJsonEntry()).getAsBoolean());
        minecraftDomainNameResolutionDismissSystemHosts.setValue(object.get(minecraftDomainNameResolutionDismissSystemHosts.getJsonEntry()).getAsBoolean());
        minecraftDomainNameResolutionDohProvider.setValue(DOHProvider.valueOf(object.get(minecraftDomainNameResolutionDohProvider.getJsonEntry()).getAsString()));
        minecraftDomainNameResolutionDohProviderUrl.setValue(object.get(minecraftDomainNameResolutionDohProviderUrl.getJsonEntry()).getAsString());
        proxyYggdrasil.setValue(object.get(proxyYggdrasil.getJsonEntry()).getAsBoolean());
        proxyRealmsApi.setValue(object.get(proxyRealmsApi.getJsonEntry()).getAsBoolean());
        proxyPlayerSkinDownload.setValue(object.get(proxyPlayerSkinDownload.getJsonEntry()).getAsBoolean());
        proxyServerResourceDownload.setValue(object.get(proxyServerResourceDownload.getJsonEntry()).getAsBoolean());
        proxyBlockListSupplier.setValue(object.get(proxyBlockListSupplier.getJsonEntry()).getAsBoolean());
        httpRemoteResolve.setValue(object.get(httpRemoteResolve.getJsonEntry()).getAsBoolean());
    }
}
