package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IMixinServerInfo {
    boolean socksProxyClient$isUseProxy();
    void socksProxyClient$setUseProxy(boolean useProxy);
}
