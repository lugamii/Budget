package dev.lugami.practice.utils.command.provider;

import dev.lugami.practice.utils.command.argument.CommandArg;
import dev.lugami.practice.utils.command.exception.CommandExitMessage;
import dev.lugami.practice.utils.command.parametric.DrinkProvider;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UUIDProvider extends DrinkProvider<UUID> {

    public static final UUIDProvider INSTANCE = new UUIDProvider();

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
    public UUID provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        UUID uuid;
        try {
            uuid = UUID.fromString(arg.get());
        } catch (IllegalArgumentException e) {
            uuid = null;
        }
        return uuid;
    }

    @Override
    public boolean allowNullArgument() {
        return false;
    }

    @Nullable
    @Override
    public UUID defaultNullValue() {
        return null;
    }

    @Override
    public String argumentDescription() {
        return "uuid";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}
