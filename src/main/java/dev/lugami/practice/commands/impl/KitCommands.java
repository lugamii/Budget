package dev.lugami.practice.commands.impl;

import dev.lugami.practice.Budget;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.command.annotation.Command;
import dev.lugami.practice.utils.command.annotation.Require;
import dev.lugami.practice.utils.command.annotation.Sender;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.entity.Player;

public class KitCommands extends CommandBase {
    public KitCommands() {
        super("kit");
    }

    @Command(name = "create", desc = "Creates a new kit.", usage = "<name>")
    @Require("budget.kit.create")
    public void createKit(@Sender Player player, String name) {
        Kit kit = new Kit(name);
        Budget.getInstance().getKitStorage().getKits().add(kit);
        player.sendMessage(CC.translate("&aSuccessfully created the kit " + name + "!"));
    }

    @Command(name = "delete", desc = "Delete a kit.", usage = "<kit>")
    @Require("budget.kit.delete")
    public void deleteKit(@Sender Player player, String name) {
        Kit kit = Budget.getInstance().getKitStorage().getByName(name);
        if (kit == null) {
            player.sendMessage(CC.translate("&cThat kit doesn't exist."));
            return;
        }
        Budget.getInstance().getKitStorage().getKits().remove(kit);
        Budget.getInstance().getKitStorage().saveKits();
        player.sendMessage(CC.translate("&aSuccessfully deleted the kit " + name + "!"));
    }

    @Command(name = "seticon", desc = "Sets a kit's icon.", usage = "<kit>")
    @Require("budget.kit.seticon")
    public void setKitIcon(@Sender Player player, String name) {
        Kit kit = Budget.getInstance().getKitStorage().getByName(name);
        if (kit == null) {
            player.sendMessage(CC.translate("&cThat kit doesn't exist."));
            return;
        }
        kit.setIcon(player.getItemInHand());
        player.sendMessage(CC.translate("&aSuccessfully set " + name + "'s icon!"));
    }

    @Command(name = "toggle", desc = "Toggles a kit.", usage = "<kit>")
    @Require("budget.kit.toggle")
    public void toggleKit(@Sender Player player, String name) {
        Kit kit = Budget.getInstance().getKitStorage().getByName(name);
        if (kit == null) {
            player.sendMessage(CC.translate("&cThat kit doesn't exist."));
            return;
        }
        kit.setEnabled(!kit.isEnabled());
        player.sendMessage(CC.translate(kit.isEnabled() ? "&aKit was enabled!" : "&cKit was disabled!"));
    }

    @Command(name = "hunger", desc = "Toggles a kit's hunger mode.", usage = "<kit>")
    @Require("budget.kit.toggle")
    public void toggleHunger(@Sender Player player, String name) {
        Kit kit = Budget.getInstance().getKitStorage().getByName(name);
        if (kit == null) {
            player.sendMessage(CC.translate("&cThat kit doesn't exist."));
            return;
        }
        kit.setHunger(!kit.isHunger());
        player.sendMessage(CC.translate(kit.isHunger() ? "&aHunger was enabled!" : "&cHunger was disabled!"));
    }

    @Command(name = "boxing", desc = "Toggles a kit's boxing mode.", usage = "<kit>")
    @Require("budget.kit.toggle")
    public void toggleBoxing(@Sender Player player, String name) {
        Kit kit = Budget.getInstance().getKitStorage().getByName(name);
        if (kit == null) {
            player.sendMessage(CC.translate("&cThat kit doesn't exist."));
            return;
        }
        kit.setBoxing(!kit.isBoxing());
        player.sendMessage(CC.translate(kit.isBoxing() ? "&aBoxing was enabled!" : "&cBoxing was disabled!"));
    }

    @Command(name = "party", desc = "Toggles a kit's party mode.", usage = "<kit>")
    @Require("budget.kit.toggle")
    public void toggleParty(@Sender Player player, String name) {
        Kit kit = Budget.getInstance().getKitStorage().getByName(name);
        if (kit == null) {
            player.sendMessage(CC.translate("&cThat kit doesn't exist."));
            return;
        }
        kit.setParty(!kit.isParty());
        player.sendMessage(CC.translate(kit.isParty() ? "&aParty mode was enabled!" : "&cParty mode was disabled!"));
    }

    @Command(name = "ranked", desc = "Toggles a kit's ranked mode.", usage = "<kit>")
    @Require("budget.kit.toggle")
    public void toggleRanked(@Sender Player player, String name) {
        Kit kit = Budget.getInstance().getKitStorage().getByName(name);
        if (kit == null) {
            player.sendMessage(CC.translate("&cThat kit doesn't exist."));
            return;
        }
        kit.setRanked(!kit.isRanked());
        player.sendMessage(CC.translate(kit.isRanked() ? "&aRanked mode was enabled!" : "&cRanked mode was disabled!"));
    }

    @Command(name = "setinventory", aliases = {"setinv"}, desc = "Sets a kit's inventory.", usage = "<kit>")
    @Require("budget.kit.setinventory")
    public void setKitInv(@Sender Player player, String name) {
        Kit kit = Budget.getInstance().getKitStorage().getByName(name);
        if (kit == null) {
            player.sendMessage(CC.translate("&cThat kit doesn't exist."));
            return;
        }
        kit.setInventory(player.getInventory().getContents());
        kit.setArmor(player.getInventory().getArmorContents());
        player.sendMessage(CC.translate("&aSuccessfully set " + name + "'s inventory!"));
    }

    @Command(name = "getinventory", aliases = {"getinv"}, desc = "Gets a kit's inventory.", usage = "<kit>")
    @Require("budget.kit.setinventory")
    public void getKitInv(@Sender Player player, String name) {
        Kit kit = Budget.getInstance().getKitStorage().getByName(name);
        if (kit == null) {
            player.sendMessage(CC.translate("&cThat kit doesn't exist."));
            return;
        }
        try {
            player.getInventory().setContents(kit.getInventory());
            player.getInventory().setArmorContents(kit.getArmor());
        } catch (IllegalArgumentException ex) {
            Budget.getInstance().getLogger().warning("Kit has length of " + kit.getInventory().length + ", causing an error!");
            Menu menu = new Menu(kit.getName() + "'s inventory", kit.getInventory().length);
            for (int i = 0; i < menu.getSize(); i++) {
                menu.setButton(i, new Button(kit.getInventory()[i]));
            }
            menu.open(player);
        }
    }


    @Command(name = "save", desc = "Saves all kits.")
    @Require("budget.kit.save")
    public void save(@Sender Player player) {
        Budget.getInstance().getKitStorage().saveKits();
        player.sendMessage(CC.translate("&aSuccessfully saved all kits!"));
    }
}
