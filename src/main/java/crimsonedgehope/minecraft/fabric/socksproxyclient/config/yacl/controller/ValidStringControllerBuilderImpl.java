package crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.StringControllerBuilderImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.Validate;

import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public class ValidStringControllerBuilderImpl extends StringControllerBuilderImpl implements ValidStringControllerBuilder {
    private Predicate<String> validityPredication = ValidStringController.VALIDITY;

    public ValidStringControllerBuilderImpl(Option<String> option) {
        super(option);
    }

    public ValidStringControllerBuilder validityPredication(Predicate<String> validityPredication) {
        Validate.notNull(validityPredication, "validityPredication cannot be null");
        this.validityPredication = validityPredication;
        return this;
    }

    @Override
    public Controller<String> build() {
        return ValidStringController.createInternal(option, validityPredication);
    }
}
