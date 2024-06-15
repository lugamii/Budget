package dev.lugami.practice.commands.impl;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import dev.lugami.practice.Budget;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.ConfigUtil;
import dev.lugami.practice.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

}
