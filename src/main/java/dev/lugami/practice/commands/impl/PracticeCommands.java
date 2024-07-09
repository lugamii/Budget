package dev.lugami.practice.commands.impl;

import dev.lugami.practice.Budget;
import dev.lugami.practice.Language;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.types.DefaultMatch;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.*;
import dev.lugami.practice.utils.command.annotation.Command;
import dev.lugami.practice.utils.command.annotation.Require;
import dev.lugami.practice.utils.command.annotation.Sender;
import dev.lugami.practice.utils.fake.FakePlayer;
import dev.lugami.practice.utils.fake.FakePlayerUtils;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.SpigotConfig;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PracticeCommands extends CommandBase {

    public PracticeCommands() {
        super("practice", new String[]{"budget"});
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

    @Command(name = "spawn", aliases = {"lobby"}, desc = "Goes to the lobby location.")
    public void spawn(@Sender Player player) {
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.getState() == ProfileState.LOBBY) {
            Budget.getInstance().getLobbyStorage().bringToLobby(player);
        } else {
            player.sendMessage(Language.CANNOT_DO_ACTION.format());
        }
    }

    @Command(name = "spectest", desc = "Used to test matches and spectating matches")
    @Require("budget.matchtest.use")
    public void matchTesting(@Sender Player player) {
        if (player.getName().equalsIgnoreCase("Misureta") || player.getName().equalsIgnoreCase("lugami1337")) {
            Kit kit = Budget.getInstance().getKitStorage().getKits().get(new SecureRandom().nextInt(Budget.getInstance().getKitStorage().getKits().size()));
            Arena arena = Budget.getInstance().getArenaStorage().getRandomArena(kit);
            FakePlayer player1 = FakePlayerUtils.spawnFakePlayer(arena.getPos1(), "Test1");
            FakePlayer player2 = FakePlayerUtils.spawnFakePlayer(arena.getPos2(), "Test2");
            Profile profile1 = new Profile(player1);
            Profile profile2 = new Profile(player2);
            DefaultMatch match = new DefaultMatch(
                    kit,
                    arena,
                    true
            );
            match.addPlayerToTeam1(player1);
            match.addPlayerToTeam2(player2);
            match.addSpectator(player, true);
            match.start();
            BukkitTask task = TaskUtil.runTaskTimer(() -> {
                player1.teleport(arena.getCuboid().getRandomLocation());
                player2.teleport(arena.getCuboid().getRandomLocation());
            }, 0L, 2L);
            TaskUtil.runTaskLater(() -> {
                int i = new SecureRandom().nextInt(2);
                match.end(i == 1 ? match.getTeam1() : match.getTeam2());
                Budget.getInstance().getProfileStorage().getProfiles().remove(profile1);
                Budget.getInstance().getProfileStorage().getProfiles().remove(profile2);
                FakePlayerUtils.removeFakePlayer("Test1");
                FakePlayerUtils.removeFakePlayer("Test2");
                task.cancel();
                player.sendMessage(CC.translate("&aThe test was completed successfully!"));
            }, 20 * 15);
        } else {
            player.sendMessage(SpigotConfig.unknownCommandMessage);
        }
    }

    @Command(name = "menutest", desc = "Menu API test.")
    @Require("budget.management.use")
    public void menu(@Sender Player p) {
        AtomicInteger i = new AtomicInteger();
        Menu menu = new Menu("&aTest", 27) {
            @Override
            public void initialize(Player player) {
                this.fillBorder();
                setButton(10, new Button(new ItemStack(Material.DIAMOND), player1 -> {
                    player1.closeInventory();
                    player1.sendMessage("You clicked the diamond!");
                }));
                setButton(11, new Button(new ItemStack(Material.GOLD_INGOT), player1 -> {
                    player1.closeInventory();
                    player1.sendMessage("You clicked the gold ingot!");
                }));
                List<String> lore = new ArrayList<>();
                lore.add("&fNumber: " + i.get());
                setButton(12, new Button(new ItemBuilder(Material.EMERALD).name("&aUpdating lore").lore(lore).build(), player1 -> {
                    player1.sendMessage(CC.translate("&aThe number is currently " + i.get() + "!"));
                }));
            }
        };
        TaskUtil.runTaskTimerAsynchronously(() -> {
            if (Menu.getOpenMenus().get(p) != menu) {
                return;
            }
            i.incrementAndGet();
        }, 0, 20);

        menu.open(p);
    }

}
