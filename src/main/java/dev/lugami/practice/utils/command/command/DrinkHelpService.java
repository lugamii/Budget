package dev.lugami.practice.utils.command.command;

import dev.lugami.practice.utils.CC;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class DrinkHelpService {

    private final DrinkCommandService commandService;
    private HelpFormatter helpFormatter;

    public DrinkHelpService(DrinkCommandService commandService) {
        this.commandService = commandService;
        this.helpFormatter = (sender, container) -> {
            sender.sendMessage(CC.CHAT_BAR);
            List<DrinkCommand> commands = new ArrayList<>();
            if (!(sender instanceof Player)) {
                for (DrinkCommand c : container.getCommands().values()) {
                    if (sender.hasPermission(c.getPermission())) {
                        commands.add(c);
                    }
                }

                // Sort commands alphabetically by name
                commands.sort(Comparator.comparing(DrinkCommand::getName));

                if (commands.isEmpty()) {
                    sender.sendMessage(CC.translate("&cYou don't have permissions for any of the commands here."));
                    return;
                }

                for (DrinkCommand c : commands) {
                    sender.sendMessage(CC.translate(" &7* &6/" + container.getName() + (!c.getName().isEmpty() ? " &6" + c.getName() : "") + " &8" + c.getMostApplicableUsage() + "&8 &8(&7&o" + c.getDescription() + "&8)"));
                }

                return;
            }

            for (DrinkCommand c : container.getCommands().values()) {
                Player player = (Player) sender;
                if (player.hasPermission(c.getPermission())) {
                    commands.add(c);
                }
            }

            // Sort commands alphabetically by name
            commands.sort(Comparator.comparing(DrinkCommand::getName));

            if (commands.isEmpty()) {
                sender.sendMessage(CC.translate("&cYou don't have permissions for any of the commands here."));
                return;
            }

            for (DrinkCommand c : commands) {
                Player player = (Player) sender;
                TextComponent msg = new TextComponent(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',
                        " &7* &6/" + container.getName() + (!c.getName().isEmpty() || !c.getName().equalsIgnoreCase("") ? " &6" + c.getName() + " &8" + c.getMostApplicableUsage() + "&8 &8(&7&o" + c.getDescription() + "&8)" : " &8(&7&o" + c.getDescription() + "&8)") ));
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + "/" + container.getName() + " " + c.getName() + " - " + ChatColor.WHITE + c.getDescription())));
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + container.getName() + " " + c.getName()));
                player.spigot().sendMessage(msg);
            }

            sender.sendMessage(CC.CHAT_BAR);
        };
    }

    public void sendHelpFor(CommandSender sender, DrinkCommandContainer container) {
        this.helpFormatter.sendHelpFor(sender, container);
    }

    public void sendUsageMessage(CommandSender sender, DrinkCommandContainer container, DrinkCommand command) {
        sender.sendMessage(getUsageMessage(container, command));
    }

    public String getUsageMessage(DrinkCommandContainer container, DrinkCommand command) {
        String usage = ChatColor.GRAY + "Usage: /" + container.getName() + " ";
        if (command.getName().length() > 0) {
            usage += command.getName() + " ";
        }
        if (command.getUsage() != null && command.getUsage().length() > 0) {
            usage += command.getUsage();
        } else {
            usage += command.getGeneratedUsage();
        }
        return usage;
    }

}
