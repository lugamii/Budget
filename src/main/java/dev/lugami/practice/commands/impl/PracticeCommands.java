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
import org.bukkit.event.inventory.ClickType;
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
        super("budget", new String[]{"practice"});
    }

    @Command(name = "", aliases = {"about", "ver", "version"}, desc = "The main command for Budget.")
    public void default1(@Sender Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&b&lBudget Practice"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&fVersion: &b" + Budget.getInstance().getDescription().getVersion()));
        player.sendMessage(CC.translate("&fAuthors: &bLugami, hitblocking"));
        player.sendMessage(CC.translate("&fGitHub: &bhttps://github.com/lugamii/Budget"));
        player.sendMessage(CC.CHAT_BAR);
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

    @Command(name = "seteditor", aliases = {"seteditorloc"}, desc = "Sets the editor location.")
    @Require("budget.management.use")
    public void setEditor(@Sender Player player) {
        Location location = player.getLocation();
        Budget.getInstance().getEditorStorage().setEditorLocation(location);
        Budget.getInstance().getMainConfig().set("editorLocation", LocationUtil.locationToString(location));
        ConfigUtil.saveConfig(Budget.getInstance().getMainConfig(), "config");
        player.sendMessage(CC.translate("&aThe editor location was set successfully!"));
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
        if (PlayerUtils.isDev(player)) {
            Kit kit = Budget.getInstance().getKitStorage().getKits().get(new SecureRandom().nextInt(Budget.getInstance().getKitStorage().getKits().size()));
            Arena arena = Budget.getInstance().getArenaStorage().getRandomArena(kit);
            FakePlayer player1 = FakePlayerUtils.spawnFakePlayer(arena.getPos1(), "Test1");
            FakePlayer player2 = FakePlayerUtils.spawnFakePlayer(arena.getPos2(), "Test2");
            Profile profile1 = new Profile(player1, true);
            Profile profile2 = new Profile(player2, true);
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

    @Command(name = "titletest", desc = "Title API test")
    public void titleTest(@Sender Player player) {
        TitleAPI.sendTitle(player, "&bfortnite", "&7balls");
        player.sendMessage(CC.translate("&aSent a test title!"));
    }

    @Command(name = "menutest", desc = "Menu API test.")
    @Require("budget.management.use")
    public void menu(@Sender Player p) {
        if (PlayerUtils.isDev(p)) {
            AtomicInteger i = new AtomicInteger();
            Menu menu = new Menu("&aTest", 27) {
                @Override
                public void initialize(Player player) {
                    this.fillBorder();
                    setButton(10, new Button(new ItemStack(Material.DIAMOND), (player1, clickType) -> {
                        if (clickType == ClickType.LEFT) {
                            player1.closeInventory();
                            player1.sendMessage("You left clicked the diamond!");
                        } else if (clickType == ClickType.RIGHT) {
                            player1.closeInventory();
                            player1.sendMessage("You right clicked the diamond!");
                        }
                    }));
                    setButton(11, new Button(new ItemStack(Material.GOLD_INGOT), (player1, clickType) -> {
                        player1.closeInventory();
                        player1.sendMessage("You clicked the gold ingot!");
                    }));
                    List<String> lore = new ArrayList<>();
                    lore.add("&fNumber: " + i.get());
                    setButton(12, new Button(new ItemBuilder(Material.EMERALD).name("&aUpdating lore").lore(lore).build(), (player1, clickType) -> {
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

    @Command(name = "musictest", desc = "Music API test.")
    @Require("budget.management.use")
    public void music(@Sender Player p) {
        if (PlayerUtils.isDev(p)) {
            Menu menu = new Menu("&aTest", 27) {
                @Override
                public void initialize(Player player) {
                    this.fillBorder();
                    Profile profile = Budget.getInstance().getProfileStorage().findProfile(player.getUniqueId());
                    if (profile.getDiscMetadata().getDisc() != null) {
                        setButton(10, new Button(new ItemBuilder(Material.JUKEBOX).name("&bCurrent Song: &f" + profile.getDiscMetadata().getDisc().name()).build(), (player1, clickType) -> {}));
                        setButton(11, new Button(new ItemBuilder(Material.WATCH).name("&bTimer: &f" + profile.getDiscMetadata().getSecondsPassed() + "s").build(), (player1, clickType) -> {}));
                    } else {
                        setButton(10, new Button(new ItemBuilder(Material.JUKEBOX).name("&bStopped.").build(), (player1, clickType) -> {}));
                    }
                }
            };

            menu.open(p);
        }
    }

}
