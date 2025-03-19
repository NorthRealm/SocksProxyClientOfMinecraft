package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.network;

import com.google.common.net.InetAddresses;
import com.llamalad7.mixinextras.sugar.Local;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.IMixinServerInfo;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.SocksProxyClientMixinPlugin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetAddress;

@Mixin(ServerInfo.class)
@Environment(EnvType.CLIENT)
public class MixinServerInfo implements IMixinServerInfo {
    @Shadow
    public String address;

    @Unique
    private static final String SPC_KEY_USEPROXY = "socksproxyclient_useproxy";

    @Unique
    private Boolean useProxy = null;

    @Override
    public boolean socksProxyClient$isUseProxy() {
        return this.useProxy;
    }

    @Override
    public void socksProxyClient$setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    @Inject(method = "toNbt", at = @At("TAIL"))
    private void saveUseProxyKey(CallbackInfoReturnable<NbtCompound> cir, @Local NbtCompound nbtCompound) {
        if (useProxy == null) {
            useProxy = true;
            try {
                InetAddress ip = InetAddresses.forString(address);
                if (ip.isLoopbackAddress()) {
                    useProxy = false;
                }
            } catch (Throwable e) {
                useProxy = true;
            }
        }
        SocksProxyClientMixinPlugin.LOGGER.debug("ServerInfo toNbt: {}, {}: {}", address, SPC_KEY_USEPROXY, useProxy);
        nbtCompound.putBoolean(SPC_KEY_USEPROXY, useProxy);
    }

    @Inject(method = "fromNbt", at = @At("TAIL"))
    private static void loadUseProxyKey(NbtCompound root, CallbackInfoReturnable<ServerInfo> cir, @Local ServerInfo serverInfo) {
        if (root.contains(SPC_KEY_USEPROXY)) {
            final boolean entry = root.getBoolean(SPC_KEY_USEPROXY);
            SocksProxyClientMixinPlugin.LOGGER.debug("ServerInfo fromNbt: {}, {}: {}", serverInfo.address, SPC_KEY_USEPROXY, entry);
            ((IMixinServerInfo) serverInfo).socksProxyClient$setUseProxy(entry);
        } else {
            try {
                InetAddress ip = InetAddresses.forString(serverInfo.address);
                if (ip.isLoopbackAddress()) {
                    SocksProxyClientMixinPlugin.LOGGER.debug("ServerInfo fromNbt: {}, useProxy: {}", serverInfo.address, false);
                    ((IMixinServerInfo) serverInfo).socksProxyClient$setUseProxy(false);
                }
            } catch (Throwable e) {
                // NO-OP
            }
        }
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void copyUseProxyKey(ServerInfo serverInfo, CallbackInfo ci) {
        final boolean entry = ((IMixinServerInfo) serverInfo).socksProxyClient$isUseProxy();
        SocksProxyClientMixinPlugin.LOGGER.debug("ServerInfo copyFrom: {} to: {}, useProxy: {}", serverInfo.address, this.address, entry);
        this.socksProxyClient$setUseProxy(entry);
    }

}
