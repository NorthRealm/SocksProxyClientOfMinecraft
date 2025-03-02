package crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl;

import crimsonedgehope.minecraft.fabric.socksproxyclient.config.ServerConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.SocksProxyClientConfigEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.controller.ValidStringControllerBuilder;
import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.doh.DOHProvider;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import net.minecraft.text.Text;

final class ServerCategory extends YACLCategory<ServerConfig> {

    SocksProxyClientConfigEntry<Boolean> proxyMinecraft;
    SocksProxyClientConfigEntry<Boolean> minecraftDomainNameResolutionUseProxy;
    SocksProxyClientConfigEntry<Boolean> minecraftDomainNameResolutionDismissSystemHosts;
    SocksProxyClientConfigEntry<DOHProvider> minecraftDomainNameResolutionDohProvider;
    SocksProxyClientConfigEntry<String> minecraftDomainNameResolutionDohProviderUrl;

    SocksProxyClientConfigEntry<Boolean> proxyYggdrasil;
    SocksProxyClientConfigEntry<Boolean> proxyRealmsApi;
    SocksProxyClientConfigEntry<Boolean> proxyPlayerSkinDownload;
    SocksProxyClientConfigEntry<Boolean> proxyServerResourceDownload;
    SocksProxyClientConfigEntry<Boolean> proxyBlockListSupplier;
    SocksProxyClientConfigEntry<Boolean> httpRemoteResolve;

    ServerCategory(YACLAccess yacl) {
        super(yacl, ServerConfig.class);
    }

    @Override
    public ConfigCategory buildConfigCategory() throws Exception {
        ConfigCategory.Builder categoryBuilder = ConfigCategory.createBuilder();

        categoryBuilder.name(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER))
                .tooltip(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_TOOLTIP));

        proxyMinecraft = entryField("proxyMinecraft", Boolean.class);
        Option<Boolean> yaclProxyMinecraft = Option.<Boolean>createBuilder()
                .name(proxyMinecraft.getEntryTranslateKey())
                .description(OptionDescription.of(proxyMinecraft.getDescriptionTranslateKey()))
                .binding(proxyMinecraft.getDefaultValue(), proxyMinecraft::getValue, proxyMinecraft::setValue)
                .controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter().coloured(true))
                .build();
        categoryBuilder.option(yaclProxyMinecraft);

        OptionGroup.Builder groupMinecraftServerDomainName = OptionGroup.createBuilder();
        groupMinecraftServerDomainName.name(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_MINECRAFTDOMAINNAMERESOLUTION));

        minecraftDomainNameResolutionUseProxy = entryField("minecraftDomainNameResolutionUseProxy", Boolean.class);
        Option<Boolean> yaclMinecraftDomainNameResolutionUseProxy = Option.<Boolean>createBuilder()
                .name(minecraftDomainNameResolutionUseProxy.getEntryTranslateKey())
                .description(OptionDescription.of(minecraftDomainNameResolutionUseProxy.getDescriptionTranslateKey()))
                .binding(minecraftDomainNameResolutionUseProxy.getDefaultValue(), minecraftDomainNameResolutionUseProxy::getValue, minecraftDomainNameResolutionUseProxy::setValue)
                .available(proxyMinecraft.getValue())
                .controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter().coloured(true))
                .build();

        minecraftDomainNameResolutionDismissSystemHosts = entryField("minecraftDomainNameResolutionDismissSystemHosts", Boolean.class);
        Option<Boolean> yaclMinecraftDomainNameResolutionDismissSystemHosts = Option.<Boolean>createBuilder()
                .name(minecraftDomainNameResolutionDismissSystemHosts.getEntryTranslateKey())
                .description(OptionDescription.of(minecraftDomainNameResolutionDismissSystemHosts.getDescriptionTranslateKey()))
                .binding(minecraftDomainNameResolutionDismissSystemHosts.getDefaultValue(), minecraftDomainNameResolutionDismissSystemHosts::getValue, minecraftDomainNameResolutionDismissSystemHosts::setValue)
                .available(proxyMinecraft.getValue())
                .controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter().coloured(true))
                .build();

        minecraftDomainNameResolutionDohProvider = entryField("minecraftDomainNameResolutionDohProvider", DOHProvider.class);
        Option<DOHProvider> yaclMinecraftDomainNameResolutionDohProvider = Option.<DOHProvider>createBuilder()
                .name(minecraftDomainNameResolutionDohProvider.getEntryTranslateKey())
                .binding(minecraftDomainNameResolutionDohProvider.getDefaultValue(), minecraftDomainNameResolutionDohProvider::getValue, minecraftDomainNameResolutionDohProvider::setValue)
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(DOHProvider.class).formatValue(v -> Text.literal(v.displayName)))
                .available(proxyMinecraft.getValue())
                .build();

        minecraftDomainNameResolutionDohProviderUrl = entryField("minecraftDomainNameResolutionDohProviderUrl", String.class);
        Option<String> yaclMinecraftDomainNameResolutionDohProviderUrl = Option.<String>createBuilder()
                .name(minecraftDomainNameResolutionDohProviderUrl.getEntryTranslateKey())
                .binding(minecraftDomainNameResolutionDohProviderUrl.getDefaultValue(), minecraftDomainNameResolutionDohProviderUrl::getValue, minecraftDomainNameResolutionDohProviderUrl::setValue)
                .controller(opt -> ValidStringControllerBuilder.create(opt).validityPredication(s -> s.startsWith("https://")))
                .available(proxyMinecraft.getValue())
                .build();

        groupMinecraftServerDomainName.option(yaclMinecraftDomainNameResolutionUseProxy);
        groupMinecraftServerDomainName.option(yaclMinecraftDomainNameResolutionDismissSystemHosts);
        groupMinecraftServerDomainName.option(yaclMinecraftDomainNameResolutionDohProvider);
        groupMinecraftServerDomainName.option(yaclMinecraftDomainNameResolutionDohProviderUrl);

        yaclProxyMinecraft.addListener((opt, v) -> {
            yaclMinecraftDomainNameResolutionUseProxy.setAvailable(v);
            yaclMinecraftDomainNameResolutionDismissSystemHosts.setAvailable(v);
            yaclMinecraftDomainNameResolutionDohProvider.setAvailable(v);
            yaclMinecraftDomainNameResolutionDohProviderUrl.setAvailable(v);
        });

        categoryBuilder.group(groupMinecraftServerDomainName.build());

        OptionGroup.Builder groupServices = OptionGroup.createBuilder();
        groupServices.name(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_SERVER_SERVICES));

        proxyYggdrasil = entryField("proxyYggdrasil", Boolean.class);
        Option<Boolean> yaclProxyYggdrasil = Option.<Boolean>createBuilder()
                .name(proxyYggdrasil.getEntryTranslateKey())
                .description(OptionDescription.of(proxyYggdrasil.getDescriptionTranslateKey()))
                .binding(proxyYggdrasil.getDefaultValue(), proxyYggdrasil::getValue, proxyYggdrasil::setValue)
                .controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter().coloured(true))
                .build();
        proxyRealmsApi = entryField("proxyRealmsApi", Boolean.class);
        Option<Boolean> yaclProxyRealmsApi = Option.<Boolean>createBuilder()
                .name(proxyRealmsApi.getEntryTranslateKey())
                .description(OptionDescription.of(proxyRealmsApi.getDescriptionTranslateKey()))
                .binding(proxyRealmsApi.getDefaultValue(), proxyRealmsApi::getValue, proxyRealmsApi::setValue)
                .controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter().coloured(true))
                .build();
        proxyPlayerSkinDownload = entryField("proxyPlayerSkinDownload", Boolean.class);
        Option<Boolean> yaclProxyPlayerSkinDownload = Option.<Boolean>createBuilder()
                .name(proxyPlayerSkinDownload.getEntryTranslateKey())
                .description(OptionDescription.of(proxyPlayerSkinDownload.getDescriptionTranslateKey()))
                .binding(proxyPlayerSkinDownload.getDefaultValue(), proxyPlayerSkinDownload::getValue, proxyPlayerSkinDownload::setValue)
                .controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter().coloured(true))
                .build();
        proxyServerResourceDownload = entryField("proxyServerResourceDownload", Boolean.class);
        Option<Boolean> yaclProxyServerResourceDownload = Option.<Boolean>createBuilder()
                .name(proxyServerResourceDownload.getEntryTranslateKey())
                .description(OptionDescription.of(proxyServerResourceDownload.getDescriptionTranslateKey()))
                .binding(proxyServerResourceDownload.getDefaultValue(), proxyServerResourceDownload::getValue, proxyServerResourceDownload::setValue)
                .controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter().coloured(true))
                .build();
        proxyBlockListSupplier = entryField("proxyBlockListSupplier", Boolean.class);
        Option<Boolean> yaclProxyBlockListSupplier = Option.<Boolean>createBuilder()
                .name(proxyBlockListSupplier.getEntryTranslateKey())
                .description(OptionDescription.of(proxyBlockListSupplier.getDescriptionTranslateKey()))
                .binding(proxyBlockListSupplier.getDefaultValue(), proxyBlockListSupplier::getValue, proxyBlockListSupplier::setValue)
                .controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter().coloured(true))
                .build();
        httpRemoteResolve = entryField("httpRemoteResolve", Boolean.class);
        Option<Boolean> yaclHttpRemoteResolve = Option.<Boolean>createBuilder()
                .name(httpRemoteResolve.getEntryTranslateKey())
                .description(OptionDescription.of(httpRemoteResolve.getDescriptionTranslateKey()))
                .binding(httpRemoteResolve.getDefaultValue(), httpRemoteResolve::getValue, httpRemoteResolve::setValue)
                .controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter().coloured(true))
                .build();

        groupServices.option(yaclProxyYggdrasil)
                .option(yaclProxyRealmsApi)
                .option(yaclProxyPlayerSkinDownload)
                .option(yaclProxyServerResourceDownload)
                .option(yaclProxyBlockListSupplier)
                .option(yaclHttpRemoteResolve);

        categoryBuilder.group(groupServices.build());

        return categoryBuilder.build();
    }
}
