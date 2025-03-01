package crimsonedgehope.minecraft.fabric.socksproxyclient.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.UpdateChecker;
import crimsonedgehope.minecraft.fabric.socksproxyclient.SocksProxyClient;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.YACLConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ModMenuEntry implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            try {
                return YACLConfigScreen.getScreen(parent);
            } catch (Throwable e) {
                SocksProxyClient.getLogger(this.getClass().getSimpleName()).error("Where's my config screen?", e);
                return null;
            }
        };
    }

    @Override
    public UpdateChecker getUpdateChecker() {
        return new SocksProxyClientUpdateChecker();
    }
}
