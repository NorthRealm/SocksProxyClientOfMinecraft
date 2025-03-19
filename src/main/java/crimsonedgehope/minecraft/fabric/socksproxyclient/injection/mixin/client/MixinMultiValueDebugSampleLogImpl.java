package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.client;

import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.IMixinMultiValueDebugSampleLogImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MultiValueDebugSampleLogImpl.class)
@Environment(EnvType.CLIENT)
public class MixinMultiValueDebugSampleLogImpl implements IMixinMultiValueDebugSampleLogImpl {
    @Unique
    private boolean pingingUseProxy;

    @Override
    public void socksProxyClient$setPingingUseProxy(boolean useProxy) {
        this.pingingUseProxy = useProxy;
    }

    @Override
    public boolean socksProxyClient$isPingingUseProxy() {
        return pingingUseProxy;
    }
}
