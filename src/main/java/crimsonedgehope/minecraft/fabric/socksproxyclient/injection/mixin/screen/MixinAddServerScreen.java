package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.screen;

import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.IMixinServerInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AddServerScreen.class)
@Environment(EnvType.CLIENT)
public abstract class MixinAddServerScreen extends Screen {
    @Shadow @Final private ServerInfo server;

    protected MixinAddServerScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void injected(CallbackInfo ci) {
        CyclingButtonWidget.Builder<Boolean> builder = CyclingButtonWidget.onOffBuilder(
                Text.literal("\u2705").formatted(Formatting.GREEN, Formatting.BOLD),
                Text.literal("\u2717").formatted(Formatting.RED, Formatting.BOLD));
        builder.initially(((IMixinServerInfo) this.server).socksProxyClient$isUseProxy());
        this.addDrawableChild(builder.build(this.width / 2 + 100, 27, 100, 20, Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_USEPROXY), (button, value) -> {
            ((IMixinServerInfo) this.server).socksProxyClient$setUseProxy(value);
        }));
    }
}
