package crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Getter
final class YACLAccess {
    private final MinecraftClient client;
    private final Screen parentScreen;
    private final YetAnotherConfigLib.Builder configBuilder;
    private YetAnotherConfigLib yacl;
    private Screen generatedScreen;

    YACLAccess(MinecraftClient client, Screen parentScreen, Text title) {
        this.client = client;
        this.parentScreen = parentScreen;
        this.configBuilder = YetAnotherConfigLib.createBuilder().title(title);
    }

    public YetAnotherConfigLib buildYacl() {
        if (Objects.isNull(yacl)) {
            yacl = configBuilder.build();
        }
        return yacl;
    }

    public Screen generateScreen() {
        if (Objects.isNull(generatedScreen)) {
            generatedScreen = buildYacl().generateScreen(parentScreen);
        }
        return generatedScreen;
    }
}
