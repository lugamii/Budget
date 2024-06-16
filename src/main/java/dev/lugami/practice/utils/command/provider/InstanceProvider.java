package dev.lugami.practice.utils.command.provider;

import dev.lugami.practice.utils.command.argument.CommandArg;
import dev.lugami.practice.utils.command.exception.CommandExitMessage;
import dev.lugami.practice.utils.command.parametric.DrinkProvider;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class InstanceProvider<T> extends DrinkProvider<T> {

    private final T instance;

    public InstanceProvider(T instance) {
        this.instance = instance;
    }

    @Override
    public boolean doesConsumeArgument() {
        return false;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public T provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        return instance;
    }

    @Override
    public String argumentDescription() {
        return instance.getClass().getSimpleName() + " (provided)";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}
