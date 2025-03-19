package crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.http;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.net.Proxy;

@Environment(EnvType.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpProxyUtils {

    public static Proxy getProxyObject() {
        return getProxyObject(true);
    }

    public static Proxy getProxyObject(boolean useProxy) {
        HttpProxy.LOGGER.debug("getProxyObject: {}", useProxy);
        if (!useProxy || !HttpProxy.INSTANCE.isFired()) {
            return Proxy.NO_PROXY;
        } else {
            return new Proxy(Proxy.Type.HTTP, HttpProxy.INSTANCE.getChannel().localAddress());
        }
    }

}
