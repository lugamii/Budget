package dev.lugami.practice.commands.impl;

import dev.lugami.practice.Budget;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.command.annotation.Command;
import dev.lugami.practice.utils.command.annotation.Require;
import dev.lugami.practice.utils.command.annotation.Sender;
import org.bukkit.entity.Player;

public class KitCommands extends CommandBase {
    public KitCommands() {
        super("kit");
    }

    @Command(name = "create", desc = "Creates a new kit.")
    @Require("budget.kit.create")
    public void createKit(@Sender Player player, String name) {
        Kit kit = new Kit(name);
        Budget.getInstance().getKitStorage().getKits().add(kit);
        player.sendMessage(CC.translate("&aSuccessfully created the kit " + name + "!"));
    }

    @Command(name = "delete", desc = "Delete a kit.")
    @Require("budget.kit.delete")
    public void deleteKit(@Sender Player player, String name) {
        Kit kit = Budget.getInstance().getKitStorage().getByName(name);
        Budget.getInstance().getKitStorage().getKits().remove(kit);
        Budget.getInstance().getKitStorage().save();
        player.sendMessage(CC.translate("&aSuccessfully deleted the kit " + name + "!"));
    }

    @Command(name = "seticon", desc = "Sets a kit's icon.")
    @Require("budget.kit.seticon")
    public void setKitIcon(@Sender Player player, String name) {
        Kit kit = Budget.getInstance().getKitStorage().getByName(name);
        kit.setIcon(player.getItemInHand());
        player.sendMessage(CC.translate("&aSuccessfully set " + name + "'s icon!"));
    }

    @Command(name = "toggle", desc = "Toggles a kit.")
    @Require("budget.kit.toggle")
    public void toggleKit(@Sender Player player, String name) {
        Kit kit = Budget.getInstance().getKitStorage().getByName(name);
        kit.setEnabled(!kit.isEnabled());
        player.sendMessage(CC.translate(kit.isEnabled() ? "&aKit was enabled!" : "&cKit was disabled!"));
    }

    @Command(name = "setinventory", aliases = {"setinv"}, desc = "Sets a kit's inventory.")
    @Require("budget.kit.setinventory")
    public void setKitInv(@Sender Player player, String name) {
        Kit kit = Budget.getInstance().getKitStorage().getByName(name);
        kit.setInventory(player.getInventory().getContents());
        kit.setArmor(player.getInventory().getArmorContents());
        player.sendMessage(CC.translate("&aSuccessfully set " + name + "'s inventory!"));
    }

    @Command(name = "save", desc = "Saves all kits.")
    @Require("budget.kit.save")
    public void save(@Sender Player player) {
        Budget.getInstance().getKitStorage().save();
        player.sendMessage(CC.translate("&aSuccessfully saved all kits!"));
    }
}
