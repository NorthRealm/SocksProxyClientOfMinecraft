package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.screen;

import com.llamalad7.mixinextras.sugar.Local;
import crimsonedgehope.minecraft.fabric.socksproxyclient.SocksProxyClient;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.MiscellaneousConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.YACLConfigScreen;
import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(MultiplayerScreen.class)
public class MixinMultiplayerScreen {
    @Shadow private ServerInfo selectedEntry;

    @Shadow private ServerList serverList;

    @Inject(method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/DirectionalLayoutWidget;refreshPositions()V",
                    shift = At.Shift.BEFORE
            ), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injected(
            CallbackInfo ci,
            ButtonWidget buttonWidget,
            ButtonWidget buttonWidget2,
            ButtonWidget buttonWidget3,
            ButtonWidget buttonWidget4,
            DirectionalLayoutWidget directionalLayoutWidget,
            AxisGridWidget axisGridWidget,
            AxisGridWidget axisGridWidget2
    ) {
        if (!MiscellaneousConfig.showButtonsInMultiplayerScreen()) {
            return;
        }
        directionalLayoutWidget.add(EmptyWidget.ofHeight(4));
        AxisGridWidget axisGridWidget3 = directionalLayoutWidget.add(new AxisGridWidget(308, 20, AxisGridWidget.DisplayAxis.HORIZONTAL));

        ButtonWidget openConfigScreenButton = ButtonWidget.builder(
                Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_SCREEN_CONFIG),
                button -> {
                    try {
                        ((AccessorScreen) this).getClient().setScreen(YACLConfigScreen.getScreen((MultiplayerScreen) (Object) this));
                    } catch (Exception e) {
                        SocksProxyClient.getLogger(this.getClass().getSimpleName()).error("Where's my config screen?", e);
                        button.active = false;
                    }
                }).width(308).build();
        ((AccessorScreen) this).invokeAddDrawableChild(openConfigScreenButton);
        axisGridWidget3.add(openConfigScreenButton);
        try {
            Class.forName("dev.isxander.yacl3.api.YetAnotherConfigLib");
        } catch (Exception e) {
            openConfigScreenButton.active = false;
        }
    }

    @Inject(method = "directConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;connect(Lnet/minecraft/client/network/ServerInfo;)V", ordinal = 1, shift = At.Shift.BEFORE))
    private void injected(boolean confirmedAction, CallbackInfo ci, @Local ServerInfo serverInfo) {
        serverInfo.copyFrom(this.selectedEntry);
        this.serverList.saveFile();
    }
}
