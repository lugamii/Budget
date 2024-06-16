package dev.lugami.practice.utils.command.provider;

import dev.lugami.practice.utils.command.argument.CommandArg;
import dev.lugami.practice.utils.command.argument.CommandArgs;
import dev.lugami.practice.utils.command.exception.CommandExitMessage;
import dev.lugami.practice.utils.command.parametric.DrinkProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class CommandArgsProvider extends DrinkProvider<CommandArgs> {

    public static final CommandArgsProvider INSTANCE = new CommandArgsProvider();

    @Override
    public boolean doesConsumeArgument() {
        return false;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public CommandArgs provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        return arg.getArgs();
    }

    @Override
    public String argumentDescription() {
        return "args";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}
