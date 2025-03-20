package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IMixinMultiValueDebugSampleLogImpl {
    boolean socksProxyClient$isPingingUseProxy();
    void socksProxyClient$setPingingUseProxy(boolean useProxy);
}
