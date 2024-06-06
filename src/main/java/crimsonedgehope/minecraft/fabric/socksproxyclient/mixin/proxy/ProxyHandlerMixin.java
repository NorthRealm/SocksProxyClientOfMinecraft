package crimsonedgehope.minecraft.fabric.socksproxyclient.mixin.proxy;

import io.netty.handler.proxy.ProxyHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.net.SocketAddress;

@Environment(EnvType.CLIENT)
@Mixin(ProxyHandler.class)
public abstract class ProxyHandlerMixin {
    @Shadow(remap = false)
    public abstract <T extends SocketAddress> T proxyAddress();

    @Shadow(remap = false)
    public abstract <T extends SocketAddress> T destinationAddress();
}
