package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access;

public interface IMixinServerInfo {
    boolean socksProxyClient$isUseProxy();
    void socksProxyClient$setUseProxy(boolean useProxy);
}
