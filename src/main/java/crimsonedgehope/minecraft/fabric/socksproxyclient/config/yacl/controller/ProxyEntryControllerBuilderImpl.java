package crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.controller;

import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.ProxyEntry;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.YACLScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.util.TriConsumer;

@Environment(EnvType.CLIENT)
public class ProxyEntryControllerBuilderImpl implements ProxyEntryControllerBuilder {
    private final Option<ProxyEntry> option;
    private TriConsumer<YACLScreen, ProxyEntry, Runnable> action;

    protected ProxyEntryControllerBuilderImpl(Option<ProxyEntry> option) {
        this.option = option;
    }

    @Override
    public ProxyEntryControllerBuilder action(TriConsumer<YACLScreen, ProxyEntry, Runnable> action) {
        Validate.notNull(action, "action cannot be null");
        this.action = action;
        return this;
    }

    @Override
    public Controller<ProxyEntry> build() {
        return ProxyEntryController.createInternal(option, action);
    }
}
