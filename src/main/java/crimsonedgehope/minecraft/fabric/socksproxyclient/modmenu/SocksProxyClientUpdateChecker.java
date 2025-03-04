package crimsonedgehope.minecraft.fabric.socksproxyclient.modmenu;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.terraformersmc.modmenu.api.UpdateChannel;
import com.terraformersmc.modmenu.api.UpdateChecker;
import com.terraformersmc.modmenu.api.UpdateInfo;
import com.terraformersmc.modmenu.util.HttpUtil;
import crimsonedgehope.minecraft.fabric.socksproxyclient.Constants;
import crimsonedgehope.minecraft.fabric.socksproxyclient.SocksProxyClient;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.MiscellaneousConfig;
import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.version.VersionComparisonOperator;
import net.fabricmc.loader.impl.util.version.VersionParser;
import net.minecraft.SharedConstants;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

@Environment(EnvType.CLIENT)
@NoArgsConstructor
public final class SocksProxyClientUpdateChecker implements UpdateChecker {

    public static final String MODRINTH = "https://modrinth.com/mod/socksproxyclient";
    private static final String METADATA_URL = "https://garment.warpedinnether.top/socksproxyclient";
    private static final Logger LOGGER = SocksProxyClient.getLogger("UpdateChecker");

    private static final HttpRequest.Builder REQUEST = HttpRequest.newBuilder().GET().uri(URI.create(METADATA_URL));

    @Override
    public UpdateInfo checkForUpdates() {
        if (!MiscellaneousConfig.shouldCheckUpdates()) {
            return null;
        }

        try {
            HttpResponse<String> response = HttpUtil.request(REQUEST, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status != HttpStatus.SC_OK) {
                throw new UnsupportedOperationException(String.format("Remote server returned %d. %d expected.", status, HttpStatus.SC_OK));
            }

            JsonObject jsonObject = new Gson().fromJson(response.body(), JsonObject.class);
            JsonObject gameVersions = jsonObject.get("versions").getAsJsonObject();
            String minecraftVersion = SharedConstants.getGameVersion().getName();
            if (!gameVersions.has(minecraftVersion)) {
                throw new UnsupportedOperationException(String.format("No version found for \"%s\"", minecraftVersion));
            }

            JsonObject releaseChannels = gameVersions.getAsJsonObject(minecraftVersion);
            String modVersion = FabricLoader.getInstance().getModContainer(Constants.MOD_ID).get().getMetadata().getVersion().getFriendlyString();
            SemanticVersion semanticModVersion = VersionParser.parseSemantic(modVersion);

            String releaseChannel = semanticModVersion.getPrereleaseKey().orElse("release");
            if (!releaseChannels.has(releaseChannel)) {
                throw new UnsupportedOperationException(String.format("No release channel found for \"%s\"", releaseChannel));
            }

            JsonArray channel = releaseChannels.get(releaseChannel).getAsJsonArray();
            for (JsonElement element : channel.asList()) {
                UpdateInfo res = compare(Version.parse(element.getAsString()), semanticModVersion, releaseChannel);
                if (Objects.nonNull(res)) {
                    return res;
                }
            }
            LOGGER.info("You are on latest version!");
            return null;
        } catch (Throwable t) {
            LOGGER.error("Failed to check for updates!", t);
        }
        return null;
    }
    
    public static UpdateInfo compare(Version newVersion, Version currrentVersion, String releaseChannel) {
        if (VersionComparisonOperator.GREATER.test(newVersion, currrentVersion)) {
            return new UpdateInfo() {
                @Override
                public boolean isUpdateAvailable() {
                    return true;
                }

                @Override
                public String getDownloadLink() {
                    return MODRINTH;
                }

                @Override
                public UpdateChannel getUpdateChannel() {
                    return switch (releaseChannel) {
                        case "release" -> UpdateChannel.RELEASE;
                        case "alpha" -> UpdateChannel.ALPHA;
                        case "beta" -> UpdateChannel.BETA;
                        default -> throw new IllegalArgumentException("Unexpected value: " + releaseChannel);
                    };
                }
            };
        }
        return null;
    }
}
