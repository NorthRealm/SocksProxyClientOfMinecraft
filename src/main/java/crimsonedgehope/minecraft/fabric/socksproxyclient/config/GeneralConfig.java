package crimsonedgehope.minecraft.fabric.socksproxyclient.config;

import com.google.gson.JsonObject;
import crimsonedgehope.minecraft.fabric.socksproxyclient.SocksProxyClient;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.ProxyCredential;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.SocksVersion;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.net.Proxy;

public final class GeneralConfig extends SocksProxyClientConfig {

    private static final GeneralConfig INSTANCE;

    static {
        INSTANCE = new GeneralConfig();
    }

    private static final Logger LOGGER = SocksProxyClient.logger(GeneralConfig.class.getSimpleName());

    public static final String CATEGORY = "general";

    private static final SocksProxyClientConfigEntry<Boolean> useProxy =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "useProxy", false);
    private static final SocksProxyClientConfigEntry<SocksVersion> socksVersion =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "socksVersion", SocksVersion.SOCKS5);
    private static final SocksProxyClientConfigEntry<String> customProxyHost =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "customProxyHost", "localhost");
    private static final SocksProxyClientConfigEntry<Integer> customProxyPort =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "customProxyPort", 1080);
    private static final SocksProxyClientConfigEntry<String> customProxyUsername =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "customProxyUsername", "");
    private static final SocksProxyClientConfigEntry<String> customProxyPassword =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "customProxyPassword", "");

    private GeneralConfig() {
        super(CATEGORY + ".json");
    }

    @Override
    public JsonObject defaultEntries() {
        JsonObject obj = new JsonObject();
        obj.addProperty(useProxy.getEntry(), useProxy.getDefaultValue());
        obj.addProperty(socksVersion.getEntry(), socksVersion.getDefaultValue().name());
        obj.addProperty(customProxyHost.getEntry(), customProxyHost.getDefaultValue());
        obj.addProperty(customProxyPort.getEntry(), customProxyPort.getDefaultValue());
        obj.addProperty(customProxyUsername.getEntry(), customProxyUsername.getDefaultValue());
        obj.addProperty(customProxyPassword.getEntry(), customProxyPassword.getDefaultValue());
        return obj;
    }

    @Override
    public void fromJsonObject(JsonObject entries) {
        useProxy.setValue(entries.get(useProxy.getEntry()).getAsBoolean());
        socksVersion.setValue(SocksVersion.valueOf(entries.get(socksVersion.getEntry()).getAsString()));
        customProxyHost.setValue(entries.get(customProxyHost.getEntry()).getAsString());
        customProxyPort.setValue(entries.get(customProxyPort.getEntry()).getAsInt());
        customProxyUsername.setValue(entries.get(customProxyUsername.getEntry()).getAsString());
        customProxyPassword.setValue(entries.get(customProxyPassword.getEntry()).getAsString());
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty(useProxy.getEntry(), useProxy.getValue());
        obj.addProperty(socksVersion.getEntry(), socksVersion.getValue().name());
        obj.addProperty(customProxyHost.getEntry(), customProxyHost.getValue());
        obj.addProperty(customProxyPort.getEntry(), customProxyPort.getValue());
        obj.addProperty(customProxyUsername.getEntry(), customProxyUsername.getValue());
        obj.addProperty(customProxyPassword.getEntry(), customProxyPassword.getValue());
        return obj;
    }

    public static boolean usingProxy() {
        return useProxy.getValue();
    }

    @NotNull
    public static Proxy getProxy() {
        return getProxy(usingProxy());
    }

    @NotNull
    public static Proxy getProxy(
            boolean useProxy
    ) {
        LOGGER.debug("useProxy {}", useProxy);

        if (!useProxy) {
            return Proxy.NO_PROXY;
        }

        return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(customProxyHost.getValue(), customProxyPort.getValue()));
    }

    public static ProxyCredential getProxyCredential() {
        return getCustomCredential();
    }

    @Getter
    private static ProxyCredential customCredential = new ProxyCredential(null, null);

    public static void setCustomCredential(@Nullable String username, @Nullable String password) {
        setCustomCredentialUsername(username);
        setCustomCredentialPassword(password);
    }

    public static void setCustomCredentialUsername(@Nullable String username) {
        customCredential.setUsername(username);
    }

    public static void setCustomCredentialPassword(@Nullable String password) {
        customCredential.setPassword(password);
    }

    @Nullable
    public static SocksVersion getSocksVersion() {
        if (!usingProxy()) {
            return null;
        }
        return socksVersion.getValue();
    }
}
