package dev.lugami.practice.utils.command.provider;

import dev.lugami.practice.utils.command.argument.CommandArg;
import dev.lugami.practice.utils.command.exception.CommandExitMessage;
import dev.lugami.practice.utils.command.parametric.DrinkProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class TextProvider extends DrinkProvider<String> {

    public static final TextProvider INSTANCE = new TextProvider();

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public String provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        StringBuilder builder = new StringBuilder(arg.get());
        while (arg.getArgs().hasNext()) {
            builder.append(" ").append(arg.getArgs().next());
        }
        return builder.toString();
    }

    @Override
    public String argumentDescription() {
        return "text";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}
