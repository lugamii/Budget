package dev.lugami.practice.commands.impl;

import dev.lugami.practice.Budget;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.command.annotation.Command;
import dev.lugami.practice.utils.command.annotation.Require;
import dev.lugami.practice.utils.command.annotation.Sender;
import org.bukkit.entity.Player;

public class ArenaCommands extends CommandBase {
    public ArenaCommands() {
        super("arena");
    }

    @Command(name = "create", desc = "Creates a new arena.")
    @Require("budget.arena.create")
    public void createArena(@Sender Player player, String name) {
        Arena arena = new Arena(name);
        Budget.getInstance().getArenaStorage().getArenas().add(arena);
        player.sendMessage(CC.translate("&aSuccessfully created the arena " + name + "!"));
    }

    @Command(name = "delete", desc = "Delete a arena.")
    @Require("budget.arena.delete")
    public void deleteArena(@Sender Player player, String name) {
        Arena arena = Budget.getInstance().getArenaStorage().getByName(name);
        Budget.getInstance().getArenaStorage().getArenas().remove(arena);
        Budget.getInstance().getArenaStorage().save();
        player.sendMessage(CC.translate("&aSuccessfully deleted the arena " + name + "!"));
    }

    @Command(name = "toggle", desc = "Toggles a arena.")
    @Require("budget.arena.toggle")
    public void toggleArena(@Sender Player player, String name) {
        Arena arena = Budget.getInstance().getArenaStorage().getByName(name);
        arena.setEnabled(!arena.isEnabled());
        player.sendMessage(CC.translate(arena.isEnabled() ? "&aArena was enabled!" : "&cArena was disabled!"));
    }

    @Command(name = "whitelistKit", desc = "Whitelists a kit on a arena.")
    @Require("budget.arena.whitelistkit")
    public void whitelistKit(@Sender Player player, String name, String kitName) {
        Arena arena = Budget.getInstance().getArenaStorage().getByName(name);
        Kit kit = Budget.getInstance().getKitStorage().getByName(kitName);
        if (arena.getWhitelistedKits().contains(kit.getName())) {
            arena.getWhitelistedKits().remove(kit.getName());
        } else {
            arena.getWhitelistedKits().add(kit.getName());
        }
        player.sendMessage(CC.translate(arena.getWhitelistedKits().contains(kit.getName()) ? "&aKit was added to the whitelist!" : "&cKit was removed from the whitelist!"));
    }

    @Command(name = "pos1", desc = "Sets a Arena's pos1.")
    @Require("budget.arena.setpos1")
    public void setArena1(@Sender Player player, String name) {
        Arena arena = Budget.getInstance().getArenaStorage().getByName(name);
        arena.setPos1(player.getLocation());
        player.sendMessage(CC.translate("&aSuccessfully set " + name + "'s 1st spawn!"));
    }

    @Command(name = "pos2", desc = "Sets a Arena's pos2.")
    @Require("budget.arena.setpos2")
    public void setArena2(@Sender Player player, String name) {
        Arena arena = Budget.getInstance().getArenaStorage().getByName(name);
        arena.setPos2(player.getLocation());
        player.sendMessage(CC.translate("&aSuccessfully set " + name + "'s 2nd spawn!"));
    }

    @Command(name = "min", desc = "Sets a Arena's min.")
    @Require("budget.arena.setmin")
    public void setMin(@Sender Player player, String name) {
        Arena arena = Budget.getInstance().getArenaStorage().getByName(name);
        arena.setMin(player.getLocation());
        player.sendMessage(CC.translate("&aSuccessfully set " + name + "'s minimum location!"));
    }

    @Command(name = "min", desc = "Sets a Arena's max.")
    @Require("budget.arena.setmax")
    public void setMax(@Sender Player player, String name) {
        Arena arena = Budget.getInstance().getArenaStorage().getByName(name);
        arena.setMax(player.getLocation());
        player.sendMessage(CC.translate("&aSuccessfully set " + name + "'s maximum location!"));
    }

    @Command(name = "save", desc = "Saves all arenas.")
    @Require("budget.arena.save")
    public void save(@Sender Player player) {
        Budget.getInstance().getArenaStorage().save();
        player.sendMessage(CC.translate("&aSuccessfully saved all arenas!"));
    }

}
