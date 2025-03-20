package crimsonedgehope.minecraft.fabric.socksproxyclient.injection.mixin.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ServerList.class)
@Environment(EnvType.CLIENT)
public interface AccessorServerList {
    @Accessor
    List<ServerInfo> getServers();

    @Accessor
    List<ServerInfo> getHiddenServers();
}
