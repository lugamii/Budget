package dev.lugami.practice.utils.command.parametric;

import dev.lugami.practice.Budget;
import lombok.Getter;
import dev.lugami.practice.utils.command.argument.CommandArg;
import dev.lugami.practice.utils.command.exception.CommandExitMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

@Getter
public abstract class DrinkProvider<T> {

    private final Budget plugin = Budget.getInstance();

    public abstract boolean doesConsumeArgument();

    public abstract boolean isAsync();

    public boolean allowNullArgument() {
        return true;
    }

    @Nullable
    public T defaultNullValue() {
        return null;
    }

    @Nullable
    public abstract T provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage;

    public abstract String argumentDescription();

    public abstract List<String> getSuggestions(@Nonnull String prefix);

    protected boolean hasAnnotation(List<? extends Annotation> list, Class<? extends Annotation> a) {
        return list.stream().anyMatch(annotation -> annotation.annotationType().equals(a));
    }

}
