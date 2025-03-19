package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.screen;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.IMixinServerInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
@Environment(EnvType.CLIENT)
public class MixinServerEntry {
    @Shadow @Final private ServerInfo server;

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;setTooltip(Lnet/minecraft/text/Text;)V"))
    private void wrapped(MultiplayerScreen instance, Text text, Operation<Void> original) {
        final List<Text> tooltips = new ArrayList<>();
        tooltips.add(text);
        tooltips.add(
                Text.translatable(TranslateKeys.SOCKSPROXYCLIENT).append(": ")
                        .append((((IMixinServerInfo) server).socksProxyClient$isUseProxy())
                                ? Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_YES)
                                : Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_NO))
        );
        instance.setTooltip(Lists.transform(tooltips, Text::asOrderedText));
    }
}
