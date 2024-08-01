package dev.lugami.practice.utils.command.argument;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandArg {

    @Getter
    private final CommandSender sender;
    private final String value;
    @Getter
    private final String label;
    @Getter
    private final CommandArgs args;

    public CommandArg(CommandSender sender, String value, CommandArgs args) {
        this.sender = sender;
        this.value = value;
        this.label = args.getLabel();
        this.args = args;
    }

    public String get() {
        return value;
    }

    public boolean isSenderPlayer() {
        return sender instanceof Player;
    }

    public Player getSenderAsPlayer() {
        return (Player) sender;
    }

}
