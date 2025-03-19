package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access;

public interface IMixinMultiValueDebugSampleLogImpl {
    boolean socksProxyClient$isPingingUseProxy();
    void socksProxyClient$setPingingUseProxy(boolean useProxy);
}
