package dev.lugami.practice.commands;

import lombok.Getter;

@Getter
public class CommandBase {

    private final String name;
    private final String[] aliases;

    public CommandBase(String n, String[] a) {
        this.name = n;
        this.aliases = a;
    }

    public CommandBase(String n) {
        this.name = n;
        this.aliases = new String[]{};
    }

}
