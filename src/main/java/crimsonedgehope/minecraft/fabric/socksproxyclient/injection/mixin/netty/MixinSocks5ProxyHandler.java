package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.proxy.Socks5ProxyHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Environment(EnvType.CLIENT)
@Mixin(Socks5ProxyHandler.class)
public abstract class MixinSocks5ProxyHandler extends MixinProxyHandler {

    @Inject(method = "newInitialMessage", at = @At("HEAD"), remap = false)
    private void injected(ChannelHandlerContext ctx, CallbackInfoReturnable<Object> cir) {
        if (destinationAddress().equals(proxyAddress())) {
            throw new IllegalStateException("You are not supposed to connect to the Socks5 proxy through itself.");
        }
    }
}
