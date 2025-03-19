package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.screen;

import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import crimsonedgehope.minecraft.fabric.socksproxyclient.injection.access.IMixinServerInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DirectConnectScreen.class)
@Environment(EnvType.CLIENT)
public abstract class MixinDirectConnectScreen extends Screen {
    @Shadow @Final @Mutable
    private ServerInfo serverEntry;

    @Unique
    private boolean useProxy;

    protected MixinDirectConnectScreen(Text title) {
        super(title);
        this.useProxy = false;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void initInjected(CallbackInfo ci) {
        CyclingButtonWidget.Builder<Boolean> builder = CyclingButtonWidget.onOffBuilder(
                Text.literal("\u2705").formatted(Formatting.GREEN, Formatting.BOLD),
                Text.literal("\u2717").formatted(Formatting.RED, Formatting.BOLD));
        // Not using the value stored in servers.dat.
        builder.initially(useProxy);
        ((IMixinServerInfo) this.serverEntry).socksProxyClient$setUseProxy(useProxy);
        this.addDrawableChild(builder.build(this.width / 2 - 50, this.height / 4 + 72 + 12, 100, 20, Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_USEPROXY), (button, value) -> {
            this.useProxy = value;
        }));
    }

    @Inject(method = "saveAndClose", at = @At("HEAD"))
    private void saveAndCloseInjected(CallbackInfo ci) {
        ((IMixinServerInfo) this.serverEntry).socksProxyClient$setUseProxy(useProxy);
    }
}
