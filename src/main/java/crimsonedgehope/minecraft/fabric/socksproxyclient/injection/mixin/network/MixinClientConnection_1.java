package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.network;

import com.llamalad7.mixinextras.sugar.Local;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.ProxySelection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.network.ClientConnection$1")
public class MixinClientConnection_1 {
    @Shadow @Final
    ClientConnection field_11663;

    @Inject(method = "initChannel", at = @At("TAIL"))
    private void injected(Channel channel, CallbackInfo ci, @Local ChannelPipeline channelPipeline) {
        ProxySelection.fire(field_11663, channelPipeline);
    }
}
