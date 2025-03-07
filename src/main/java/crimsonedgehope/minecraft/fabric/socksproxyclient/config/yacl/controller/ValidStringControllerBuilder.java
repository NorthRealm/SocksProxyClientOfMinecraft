package crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public interface ValidStringControllerBuilder extends StringControllerBuilder {
    ValidStringControllerBuilder validityPredication(Predicate<String> validityPredication);

    static ValidStringControllerBuilder create(Option<String> option) {
        return new ValidStringControllerBuilderImpl(option);
    }
}
