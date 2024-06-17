package dev.lugami.practice.commands.impl;

import dev.lugami.practice.Budget;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.ConfigUtil;
import dev.lugami.practice.utils.LocationUtil;
import dev.lugami.practice.utils.command.annotation.Command;
import dev.lugami.practice.utils.command.annotation.Require;
import dev.lugami.practice.utils.command.annotation.Sender;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ManagementCommands extends CommandBase {

    public ManagementCommands() {
        super("management", new String[]{"manage"});
    }

    @Command(name = "setspawn", aliases = {"setlobby"}, desc = "Sets the lobby location.")
    @Require("budget.management.use")
    public void setSpawn(@Sender Player player) {
        Location location = player.getLocation();
        Budget.getInstance().getLobbyStorage().setLobbyLocation(location);
        Budget.getInstance().getMainConfig().set("spawnLocation", LocationUtil.locationToString(location));
        ConfigUtil.saveConfig(Budget.getInstance().getMainConfig(), "config");
        player.sendMessage(CC.translate("&aThe lobby location was set successfully!"));
    }

    @Command(name = "menutest", desc = "Menu API test.")
    @Require("budget.management.use")
    public void menu(@Sender Player p) {
        Menu menu = new Menu("&aTest", 9);
        menu.setButton(3, new Button(new ItemStack(Material.DIAMOND), player -> {
            player.closeInventory();
            player.sendMessage("You clicked the diamond!");
        }));
        menu.setButton(5, new Button(new ItemStack(Material.GOLD_INGOT), player -> {
            player.closeInventory();
            player.sendMessage("You clicked the gold ingot!");
        }));
        menu.open(p);
    }

}
