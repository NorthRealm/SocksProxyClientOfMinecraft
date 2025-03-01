package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.screen;

import com.llamalad7.mixinextras.sugar.Local;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.IMixinClientConnection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.client.gui.screen.multiplayer.ConnectScreen$1")
public class MixinConnectScreen_1 {

    @Shadow @Final
    ServerInfo field_40415;

    @Inject(method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/ClientConnection;connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/network/ClientConnection;)Lio/netty/channel/ChannelFuture;",
                    shift = At.Shift.BEFORE
            )
    )
    private void injected(CallbackInfo ci, @Local ClientConnection clientConnection) {
        ((IMixinClientConnection) clientConnection).socksProxyClient$setServerInfo(field_40415);
    }
}
