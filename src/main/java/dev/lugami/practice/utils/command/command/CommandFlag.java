package dev.lugami.practice.utils.command.command;

import lombok.Getter;

import javax.annotation.Nullable;

public class CommandFlag {

    public static final char FLAG_PREFIX = '-';
    @Getter
    private final char character;
    @Nullable
    private final String value;

    public CommandFlag(char character) {
        this(character, null);
    }

    public CommandFlag(char character, @Nullable String value) {
        this.character = character;
        this.value = value;
    }

    @Nullable
    public String getValue() {
        return value;
    }

    public String flagPrefixToString() {
        return String.valueOf(new char[]{FLAG_PREFIX, character});
    }

}
