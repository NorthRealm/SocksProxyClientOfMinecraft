package crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.string.StringController;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public class ValidStringController extends StringController {

    public static final Predicate<String> VALIDITY = s -> !(Objects.isNull(s) || s.isBlank() || s.isEmpty());

    private final Predicate<String> validityPredication;

    public ValidStringController(Option<String> option, Predicate<String> validityPredication) {
        super(option);
        this.validityPredication = validityPredication;
    }

    @Override
    public boolean isInputValid(String input) {
        return validityPredication.test(input);
    }

    @ApiStatus.Internal
    public static ValidStringController createInternal(Option<String> option, Predicate<String> validityPredication) {
        return new ValidStringController(option, validityPredication);
    }
}
