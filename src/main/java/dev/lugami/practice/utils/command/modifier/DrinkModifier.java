package dev.lugami.practice.utils.command.modifier;

import dev.lugami.practice.utils.command.command.CommandExecution;
import dev.lugami.practice.utils.command.exception.CommandExitMessage;
import dev.lugami.practice.utils.command.parametric.CommandParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface DrinkModifier<T> {

    Optional<T> modify(@Nonnull CommandExecution execution, @Nonnull CommandParameter commandParameter, @Nullable T argument) throws CommandExitMessage;

}
