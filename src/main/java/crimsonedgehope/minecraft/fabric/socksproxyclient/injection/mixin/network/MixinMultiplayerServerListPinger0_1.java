package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.network;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.IMixinMultiValueDebugSampleLogImpl;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.IMixinServerInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.net.InetSocketAddress;

@Mixin(MultiplayerServerListPinger.class)
@Environment(EnvType.CLIENT)
public class MixinMultiplayerServerListPinger0_1 {
    @WrapOperation(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/util/profiler/MultiValueDebugSampleLogImpl;)Lnet/minecraft/network/ClientConnection;"))
    private ClientConnection setUseProxy(InetSocketAddress address, boolean useEpoll, MultiValueDebugSampleLogImpl packetSizeLog, Operation<ClientConnection> original, @Local(argsOnly = true) ServerInfo serverInfo) {
        if (packetSizeLog == null) {
            packetSizeLog = new MultiValueDebugSampleLogImpl(1);
        }

        ((IMixinMultiValueDebugSampleLogImpl) packetSizeLog).socksProxyClient$setUseProxy(((IMixinServerInfo) serverInfo).socksProxyClient$isUseProxy());
        return original.call(address, useEpoll, packetSizeLog);
    }
}
