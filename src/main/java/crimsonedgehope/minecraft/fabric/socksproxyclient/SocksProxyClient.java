package crimsonedgehope.minecraft.fabric.socksproxyclient;

import crimsonedgehope.minecraft.fabric.socksproxyclient.config.ConfigUtils;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.http.HttpProxy;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class SocksProxyClient implements ClientModInitializer {

	public static void preInit() throws Exception {
		ConfigUtils.loadAllConfig();
		HttpProxy.INSTANCE.fire();
	}

	@Override
	public void onInitializeClient() {

	}

	public static Logger getLogger(final String path) {
		return LoggerFactory.getLogger("SocksProxyClient/" + path);
	}
}
