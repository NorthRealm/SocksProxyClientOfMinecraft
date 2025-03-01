package crimsonedgehope.minecraft.fabric.socksproxyclient.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import crimsonedgehope.minecraft.fabric.socksproxyclient.SocksProxyClient;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.ProxyEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.SocksProxyClientConfigEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.socks.SocksVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public final class GeneralConfig extends SocksProxyClientConfig {

    private static final GeneralConfig INSTANCE;

    static {
        INSTANCE = new GeneralConfig();
    }

    private static final Logger LOGGER = SocksProxyClient.getLogger(GeneralConfig.class.getSimpleName());

    public static final String CATEGORY = "general";

    private static final SocksProxyClientConfigEntry<Boolean> useProxy =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "useProxy",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_USEPROXY), false);
    private static final SocksProxyClientConfigEntry<List<ProxyEntry>> proxies =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "proxies",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXIES),
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXIES_TOOLTIP),
                    new ArrayList<>() {{
                        add(new ProxyEntry(SocksVersion.SOCKS5, new InetSocketAddress("localhost", 1080)));
                    }});
    private static final SocksProxyClientConfigEntry<List<String>> httpTestSubjects =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "httpTestSubjects",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_TEST_HTTPSUBJECTS),
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_TEST_HTTPSUBJECTS_TOOLTIP),
                    new ArrayList<>() {{
                        add("https://api.mojang.com");
                        add("https://ipinfo.io");
                        add("http://connectivitycheck.gstatic.com/generate_204");
                    }});
    private static final SocksProxyClientConfigEntry<List<String>> minecraftTestSubjects =
            new SocksProxyClientConfigEntry<>(INSTANCE.getClass(), "minecraftTestSubjects",
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_TEST_MINECRAFTSUBJECTS),
                    Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_TEST_MINECRAFTSUBJECTS_TOOLTIP),
                    new ArrayList<>() {{
                        add("play.cubecraft.net");
                    }});

    private GeneralConfig() {
        super(CATEGORY + ".json");
    }

    @Override
    public JsonObject defaultEntries() {
        JsonObject obj = new JsonObject();
        obj.addProperty(useProxy.getJsonEntry(), useProxy.getDefaultValue());

        {
            JsonArray proxyJsonArray = new JsonArray();
            proxies.getDefaultValue().forEach(entry -> {
                JsonObject proxyObj = new JsonObject();
                proxyObj.addProperty("version", entry.getVersion().name());
                proxyObj.addProperty("host", ((InetSocketAddress) entry.getProxy().address()).getHostString());
                proxyObj.addProperty("port", ((InetSocketAddress) entry.getProxy().address()).getPort());
                proxyObj.addProperty("username", entry.getSocksProxyCredential().getUsername());
                proxyObj.addProperty("password", entry.getSocksProxyCredential().getPassword());
                proxyJsonArray.add(proxyObj);
            });
            obj.add(proxies.getJsonEntry(), proxyJsonArray);
        }

        {
            JsonArray httpTestSubjectJsonArray = new JsonArray();
            httpTestSubjects.getDefaultValue().forEach(httpTestSubjectJsonArray::add);
            obj.add(httpTestSubjects.getJsonEntry(), httpTestSubjectJsonArray);
        }

        {
            JsonArray minecraftTestSubjectJsonArray = new JsonArray();
            minecraftTestSubjects.getDefaultValue().forEach(minecraftTestSubjectJsonArray::add);
            obj.add(minecraftTestSubjects.getJsonEntry(), minecraftTestSubjectJsonArray);
        }

        return obj;
    }

    @Override
    public void fromJsonObject(JsonObject entries) {
        useProxy.setValue(entries.get(useProxy.getJsonEntry()).getAsBoolean());

        {
            List<ProxyEntry> proxyEntryArrayList = new ArrayList<>();
            JsonArray array = (JsonArray) entries.get(proxies.getJsonEntry());

            SocksVersion version;
            String host;
            int port;
            String username;
            String password;

            for (JsonElement element : array) {
                JsonObject proxyObj = (JsonObject) element;
                try {
                    version = SocksVersion.valueOf(proxyObj.get("version").getAsString());
                } catch (Exception e) {
                    version = SocksVersion.SOCKS5;
                }
                host = proxyObj.get("host").getAsString();
                if (Objects.isNull(host)) {
                    host = "localhost";
                }
                try {
                    port = proxyObj.get("port").getAsInt();
                } catch (Exception e) {
                    port = 1080;
                }
                try {
                    username = proxyObj.get("username").getAsString();
                } catch (Exception e) {
                    username = null;
                }
                try {
                    password = proxyObj.get("password").getAsString();
                } catch (Exception e) {
                    password = null;
                }

                proxyEntryArrayList.add(new ProxyEntry(version, new InetSocketAddress(host, port), username, password));
            }
            proxies.setValue(proxyEntryArrayList);
        }

        {
            List<String> httpTestSubjectArrayList = new ArrayList<>();
            JsonArray array = (JsonArray) entries.get(httpTestSubjects.getJsonEntry());
            for (JsonElement element : array) {
                httpTestSubjectArrayList.add(element.getAsString());
            }
            httpTestSubjects.setValue(httpTestSubjectArrayList);
        }

        {
            List<String> minecraftTestSubjectArrayList = new ArrayList<>();
            JsonArray array = (JsonArray) entries.get(minecraftTestSubjects.getJsonEntry());
            for (JsonElement element : array) {
                minecraftTestSubjectArrayList.add(element.getAsString());
            }
            minecraftTestSubjects.setValue(minecraftTestSubjectArrayList);
        }
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty(useProxy.getJsonEntry(), useProxy.getValue());

        {
            JsonArray proxyJsonArray = new JsonArray();
            proxies.getValue().forEach(entry -> {
                JsonObject proxyObj = new JsonObject();
                proxyObj.addProperty("version", entry.getVersion().name());
                proxyObj.addProperty("host", ((InetSocketAddress) entry.getProxy().address()).getHostString());
                proxyObj.addProperty("port", ((InetSocketAddress) entry.getProxy().address()).getPort());
                proxyObj.addProperty("username", entry.getSocksProxyCredential().getUsername());
                proxyObj.addProperty("password", entry.getSocksProxyCredential().getPassword());
                proxyJsonArray.add(proxyObj);
            });
            obj.add(proxies.getJsonEntry(), proxyJsonArray);
        }

        {
            JsonArray httpTestSubjectJsonArray = new JsonArray();
            httpTestSubjects.getValue().forEach(httpTestSubjectJsonArray::add);
            obj.add(httpTestSubjects.getJsonEntry(), httpTestSubjectJsonArray);
        }

        {
            JsonArray minecraftTestSubjectJsonArray = new JsonArray();
            minecraftTestSubjects.getValue().forEach(minecraftTestSubjectJsonArray::add);
            obj.add(minecraftTestSubjects.getJsonEntry(), minecraftTestSubjectJsonArray);
        }

        return obj;
    }

    public static boolean usingProxy() {
        return useProxy.getValue();
    }

    public static List<ProxyEntry> getProxyEntry() {
        return getProxyEntry(usingProxy());
    }

    public static List<ProxyEntry> getProxyEntry(boolean useProxy) {
        LOGGER.debug("useProxy: {}", useProxy);

        if (!useProxy) {
            return new ArrayList<>();
        }
        return proxies.getValue();
    }

    public static List<String> getHTTPTestSubjects() {
        return httpTestSubjects.getValue();
    }

    public static List<String> getMinecraftTestSubjects() {
        return minecraftTestSubjects.getValue();
    }
}
