package crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl;

import crimsonedgehope.minecraft.fabric.socksproxyclient.config.ConfigUtils;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.GeneralConfig;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.ProxyEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.SocksProxyClientConfigEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.controller.ProxyEntryControllerBuilder;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.controller.ValidStringControllerBuilder;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.screen.ProxyEntryEditScreen;
import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.socks.SocksUtils;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.List;

final class GeneralCategory extends YACLCategory<GeneralConfig> {

    SocksProxyClientConfigEntry<Boolean> useProxy;
    SocksProxyClientConfigEntry<List> proxies;
    SocksProxyClientConfigEntry<List> httpTestSubjects;
    SocksProxyClientConfigEntry<List> minecraftTestSubjects;

    GeneralCategory(YACLAccess yacl) {
        super(yacl, GeneralConfig.class);
    }

    @Override
    public ConfigCategory buildConfigCategory() throws Exception {
        ConfigCategory.Builder categoryBuilder = ConfigCategory.createBuilder();

        categoryBuilder.name(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL))
                .tooltip(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_TOOLTIP));

        useProxy = entryField("useProxy", Boolean.class);
        Option<Boolean> yaclUseProxy = Option.<Boolean>createBuilder()
                .name(useProxy.getEntryTranslateKey())
                .binding(useProxy.getDefaultValue(), useProxy::getValue, useProxy::setValue)
                .controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter().coloured(true))
                .flag(OptionFlag.GAME_RESTART)
                .build();
        categoryBuilder.option(yaclUseProxy);

        proxies = entryField("proxies", List.class);
        ListOption<ProxyEntry> yaclProxies = ListOption.<ProxyEntry>createBuilder()
                .name(proxies.getEntryTranslateKey())
                .description(OptionDescription.of(proxies.getDescriptionTranslateKey()))
                .initial((ProxyEntry) proxies.getDefaultValue().get(0))
                .binding((List<ProxyEntry>) proxies.getDefaultValue(), proxies::getValue, proxies::setValue)
                .collapsed(false)
                .controller(opt -> ProxyEntryControllerBuilder.create((Option<ProxyEntry>) opt).action((screen, entry, callback) -> {
                    MinecraftClient.getInstance().setScreen(new ProxyEntryEditScreen(screen, entry, callback));
                }))
                .insertEntriesAtEnd(true)
                .flag(OptionFlag.GAME_RESTART)
                .available(useProxy.getValue())
                .build();

        categoryBuilder.group(yaclProxies);

        OptionGroup.Builder proxyGroupBuilder = OptionGroup.createBuilder()
                .name(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY));

        ButtonOption yaclTestReachability = ButtonOption.createBuilder()
                .name(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_TEST))
                .description(OptionDescription.of(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_TEST_TOOLTIP)))
                .available(true)
                .action((screen, opt) -> SocksUtils.test())
                .available(useProxy.getValue())
                .build();

        httpTestSubjects = entryField("httpTestSubjects", List.class);
        ListOption<String> yaclHTTPTestSubjects = ListOption.<String>createBuilder()
                .name(httpTestSubjects.getEntryTranslateKey())
                .description(OptionDescription.of(httpTestSubjects.getDescriptionTranslateKey()))
                .initial("")
                .binding((List<String>) httpTestSubjects.getDefaultValue(), httpTestSubjects::getValue, httpTestSubjects::setValue)
                .collapsed(false)
                .controller(opt -> StringControllerBuilder.create((Option<String>) opt))
                .insertEntriesAtEnd(true)
                .available(useProxy.getValue())
                .build();

        minecraftTestSubjects = entryField("minecraftTestSubjects", List.class);
        ListOption<String> yaclMinecraftTestSubjects = ListOption.<String>createBuilder()
                .name(minecraftTestSubjects.getEntryTranslateKey())
                .description(OptionDescription.of(minecraftTestSubjects.getDescriptionTranslateKey()))
                .initial("")
                .binding((List<String>) minecraftTestSubjects.getDefaultValue(), minecraftTestSubjects::getValue, minecraftTestSubjects::setValue)
                .collapsed(false)
                .controller(opt -> ValidStringControllerBuilder.create((Option<String>) opt).validityPredication(ConfigUtils.minecraftAddressValidity))
                .insertEntriesAtEnd(true)
                .available(useProxy.getValue())
                .build();

        proxyGroupBuilder.option(yaclTestReachability);

        yaclUseProxy.addListener((opt, v) -> {
            yaclProxies.setAvailable(v);
            yaclTestReachability.setAvailable(v);
            yaclHTTPTestSubjects.setAvailable(v);
            yaclMinecraftTestSubjects.setAvailable(v);
        });

        categoryBuilder.group(proxyGroupBuilder.build());

        categoryBuilder.group(yaclHTTPTestSubjects);
        categoryBuilder.group(yaclMinecraftTestSubjects);

        return categoryBuilder.build();
    }
}
