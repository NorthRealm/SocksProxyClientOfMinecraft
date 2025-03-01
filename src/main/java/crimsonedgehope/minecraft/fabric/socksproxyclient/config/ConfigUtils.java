package crimsonedgehope.minecraft.fabric.socksproxyclient.config;

import com.google.common.net.HostAndPort;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.SocksProxyClientConfigEntry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Field;
import java.net.IDN;
import java.util.Objects;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigUtils {

    public static final Predicate<HostAndPort> hostAndPortValidity = (hostAndPort) -> {
        String string = hostAndPort.getHost();
        int port = hostAndPort.getPort();
        if (!string.isEmpty() && port > 0 && port <= 65535) {
            IDN.toASCII(string);
            return true;
        }
        return false;
    };

    public static final Predicate<String> minecraftAddressValidity = (address) -> hostAndPortValidity.test(HostAndPort.fromString(address).withDefaultPort(25565));

    public static final Predicate<String> addressValidity = (address) -> hostAndPortValidity.test(HostAndPort.fromString(address).withDefaultPort(0));

    public static <C extends SocksProxyClientConfig> C getConfigInstance(final Class<C> clazz) throws Exception {
        Field field = clazz.getDeclaredField("INSTANCE");
        field.setAccessible(true);
        Object instance = field.get(null);
        Objects.requireNonNull(instance);
        if (!clazz.isInstance(instance)) {
            instance = null;
        }
        return (C) instance;
    }

    public static void loadAllConfig() throws Exception {
        getConfigInstance(GeneralConfig.class).load();
        getConfigInstance(ServerConfig.class).load();
        getConfigInstance(MiscellaneousConfig.class).load();
    }

    public static void saveAllConfig() throws Exception {
        getConfigInstance(GeneralConfig.class).save();
        getConfigInstance(ServerConfig.class).save();
        getConfigInstance(MiscellaneousConfig.class).save();
    }

    public static <T extends SocksProxyClientConfig> String getCategoryField(Class<T> clazz) throws Exception {
        String category = null;
        Field field = clazz.getDeclaredField("CATEGORY");
        field.setAccessible(true);
        category = (String) field.get(null);
        if (Objects.isNull(category)) {
            category = clazz.getSimpleName();
        }
        return category;
    }

    public static SocksProxyClientConfigEntry<?> getEntryField(
            final Class<? extends SocksProxyClientConfig> configClass, final String fieldName) throws Exception {
        return getConfigInstance(configClass).getEntryField(fieldName);
    }

    public static <T> SocksProxyClientConfigEntry<T> getEntryField(
            final Class<? extends SocksProxyClientConfig> configClass, final String fieldName, final Class<T> valueType) throws Exception {
        return getConfigInstance(configClass).getEntryField(fieldName, valueType);
    }
}
