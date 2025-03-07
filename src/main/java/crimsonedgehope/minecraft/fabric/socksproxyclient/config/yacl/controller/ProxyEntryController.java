package crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.controller;

import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.ProxyEntry;
import crimsonedgehope.minecraft.fabric.socksproxyclient.i18n.TranslateKeys;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.ApiStatus;

import java.net.InetSocketAddress;

@Environment(EnvType.CLIENT)
public class ProxyEntryController implements Controller<ProxyEntry> {
    private final Option<ProxyEntry> option;
    private final TriConsumer<YACLScreen, ProxyEntry, Runnable> action;

    public ProxyEntryController(Option<ProxyEntry> option, TriConsumer<YACLScreen, ProxyEntry, Runnable> action) {
        this.option = option;
        this.action = action;
    }

    @Override
    public Option<ProxyEntry> option() {
        return option;
    }

    public TriConsumer<YACLScreen, ProxyEntry, Runnable> action() {
        return action;
    }

    @Override
    public Text formatValue() {
        InetSocketAddress sa = (InetSocketAddress) getEntry().getProxy().address();
        return Text.translatable(TranslateKeys.SOCKSPROXYCLIENT_CONFIG_GENERAL_PROXY_EDIT,
                getEntry().getVersion().description, sa.getHostString() + ":" + sa.getPort());
    }

    public ProxyEntry getEntry() {
        return option().pendingValue();
    }

    public void setEntry(ProxyEntry entry) {
        option().requestSet(entry);
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new ProxyEntryControllerElement(this, screen, widgetDimension);
    }

    public static class ProxyEntryControllerElement extends ControllerWidget<ProxyEntryController> {
        private String buttonString;

        public ProxyEntryControllerElement(ProxyEntryController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
            buttonString = control.formatValue().getString().toLowerCase();
        }

        public void executeAction() {
            ProxyEntry e0 = control.getEntry();
            ProxyEntry e1 = new ProxyEntry(
                    e0.getVersion(),
                    InetSocketAddress.createUnresolved(
                            ((InetSocketAddress) e0.getProxy().address()).getHostString(),
                            ((InetSocketAddress) e0.getProxy().address()).getPort()
                    ),
                    e0.getSocksProxyCredential().getUsername(),
                    e0.getSocksProxyCredential().getPassword());
            playDownSound();
            control.action().accept(screen, e1, () -> control.setEntry(e1));
            buttonString = control.formatValue().getString().toLowerCase();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOver(mouseX, mouseY) && isAvailable()) {
                executeAction();
                return true;
            }
            return false;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!focused) {
                return false;
            }

            if (keyCode == InputUtil.GLFW_KEY_ENTER || keyCode == InputUtil.GLFW_KEY_SPACE || keyCode == InputUtil.GLFW_KEY_KP_ENTER) {
                executeAction();
                return true;
            }

            return false;
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }

        @Override
        public boolean canReset() {
            return false;
        }

        @Override
        public boolean matchesSearch(String query) {
            return super.matchesSearch(query) || buttonString.contains(query);
        }
    }

    @ApiStatus.Internal
    public static ProxyEntryController createInternal(Option<ProxyEntry> option, TriConsumer<YACLScreen, ProxyEntry, Runnable> action) {
        return new ProxyEntryController(option, action);
    }
}
