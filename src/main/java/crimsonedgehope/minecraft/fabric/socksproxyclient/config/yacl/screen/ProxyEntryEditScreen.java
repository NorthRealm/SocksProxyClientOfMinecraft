package crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.screen;

import com.google.common.net.HostAndPort;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.ConfigUtils;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.ProxyEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.socks.SocksProxyCredential;
import crimsonedgehope.minecraft.fabric.socksproxyclient.proxy.socks.SocksVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ProxyEntryEditScreen extends Screen {
    private final Screen parent;

    @Nullable
    private ProxyEntry entry;

    private CyclingButtonWidget<Object> setSocksVersionButton;
    /* Host and port */
    private TextFieldWidget proxyAddressField;
    private TextFieldWidget usernameField;
    private TextFieldWidget passwordField;
    private ButtonWidget setButton;

    private final Runnable callback;

    public ProxyEntryEditScreen(Screen parent, @Nullable ProxyEntry entry, @Nullable Runnable callback) {
        super(Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY));
        this.parent = parent;
        this.entry = entry;
        this.callback = callback;
    }

    @Override
    protected void init() {
        this.proxyAddressField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 46, 200, 20,
                Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_PROXYADDRESS));
        this.proxyAddressField.setMaxLength(262);
        String p = "";
        this.proxyAddressField.setText(p);
        if (Objects.nonNull(entry)) {
            InetSocketAddress sa = (InetSocketAddress) entry.getProxy().address();
            if (Objects.nonNull(sa.getHostString())) {
                p = sa.getHostString() + ":" + sa.getPort();
            }
            this.proxyAddressField.setText(p);
        }
        this.proxyAddressField.setChangedListener(s -> updateSetButton());
        this.addSelectableChild(this.proxyAddressField);

        this.usernameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 86, 200, 20,
                Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_USERNAME));
        this.usernameField.setMaxLength(255);
        this.usernameField.setText(Objects.isNull(entry) ? "" : entry.getSocksProxyCredential().getUsername());
        this.addSelectableChild(this.usernameField);

        this.passwordField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 126, 200, 20,
                Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_PASSWORD));
        this.passwordField.setMaxLength(255);
        this.passwordField.setRenderTextProvider((string, firstCharacterIndex) -> Text.of(new StringBuilder().repeat("*", string.length()).toString()).asOrderedText());
        this.passwordField.setText(Objects.isNull(entry) ? "" : entry.getSocksProxyCredential().getPassword());
        this.addSelectableChild(this.passwordField);

        this.setSocksVersionButton = this.addDrawableChild(CyclingButtonWidget.builder(o -> Text.literal(o.toString())).values((Object[]) SocksVersion.values())
                .initially(SocksVersion.SOCKS5).build(this.width / 2 - 100, this.height / 4 + 92 + 18, 200, 20,
                        Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_SOCKSVERSION),
                        (button, socksVersion) -> {
                            entry.setVersion((SocksVersion) socksVersion);
                            this.passwordField.active = !socksVersion.equals(SocksVersion.SOCKS4);
                        }));
        this.setSocksVersionButton.setValue(Objects.nonNull(entry) ? entry.getVersion() : SocksVersion.SOCKS5);

        this.setButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> setAndClose())
                .dimensions(this.width / 2 - 100, this.height / 4 + 116 + 18, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close())
                .dimensions(this.width / 2 - 100, this.height / 4 + 140 + 18, 200, 20).build());

        this.updateSetButton();
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.proxyAddressField);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.proxyAddressField.getText();
        String string2 = this.usernameField.getText();
        String string3 = this.passwordField.getText();
        this.init(client, width, height);
        this.proxyAddressField.setText(string);
        this.usernameField.setText(string2);
        this.passwordField.setText(string3);
    }

    @Override
    public void close() {
        if (Objects.nonNull(this.callback)) {
            this.callback.run();
        }
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 17, 16777215);
        context.drawTextWithShadow(this.textRenderer, Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_PROXYADDRESS), this.width / 2 - 100 + 1, 33, 10526880);
        context.drawTextWithShadow(this.textRenderer, Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_USERNAME), this.width / 2 - 100 + 1, 74, 10526880);
        context.drawTextWithShadow(this.textRenderer, Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_PASSWORD), this.width / 2 - 100 + 1, 115, 10526880);
        this.proxyAddressField.render(context, mouseX, mouseY, delta);
        this.usernameField.render(context, mouseX, mouseY, delta);
        this.passwordField.render(context, mouseX, mouseY, delta);
    }

    private void setAndClose() {
        HostAndPort hostAndPort = HostAndPort.fromString(this.proxyAddressField.getText());
        if (Objects.isNull(entry)) {
            entry = new ProxyEntry(
                    (SocksVersion) this.setSocksVersionButton.getValue(),
                    InetSocketAddress.createUnresolved(hostAndPort.getHost(), hostAndPort.getPort()),
                    this.usernameField.getText(),
                    this.passwordField.getText());
        } else {
            entry.setVersion((SocksVersion) this.setSocksVersionButton.getValue());
            entry.setProxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(hostAndPort.getHost(), hostAndPort.getPort())));
            entry.setSocksProxyCredential(new SocksProxyCredential(this.usernameField.getText(), this.passwordField.getText()));
        }
        this.close();
    }

    private void updateSetButton() {
        this.setButton.active = ((Supplier<Boolean>) () -> {
            try {
                return ConfigUtils.addressValidity.test(this.proxyAddressField.getText());
            } catch (Exception e) {
                // No op
            }
            return false;
        }).get();
    }
}
