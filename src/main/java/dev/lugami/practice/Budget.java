package dev.lugami.practice;

import com.google.common.collect.Table;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import dev.lugami.practice.board.ScoreboardProvider;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.storage.*;
import dev.lugami.practice.task.MatchEnderpearlTask;
import dev.lugami.practice.task.MatchSnapshotTask;
import dev.lugami.practice.task.QueueTask;
import dev.lugami.practice.utils.ClassUtils;
import dev.lugami.practice.utils.ConfigUtil;
import dev.lugami.practice.utils.EntityHider;
import dev.lugami.practice.utils.TaskUtil;
import dev.lugami.practice.utils.command.Drink;
import dev.lugami.practice.utils.command.command.CommandService;
import dev.lugami.practice.utils.menu.Button;
import dev.lugami.practice.utils.menu.Menu;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

@Getter
@Setter
public class Budget extends JavaPlugin {

    @Getter
    private static Budget instance;
    private YamlConfiguration mainConfig, kitConfig, arenaConfig, scoreboardConfig, messagesConfig;
    private ProfileStorage profileStorage;
    private LobbyStorage lobbyStorage;
    private KitStorage kitStorage;
    private ArenaStorage arenaStorage;
    private MatchStorage matchStorage;
    private HotbarStorage hotbarStorage;
    private QueueStorage queueStorage;
    private PartyStorage partyStorage;
    private LeaderboardsStorage leaderboardsStorage;
    private Assemble assemble;
    private EntityHider entityHider;
    private CommandService drink;
    private MongoDatabase mongoDatabase;

    @Override
    public void onEnable() {
        instance = this;
        this.mainConfig = ConfigUtil.createConfig("config");
        this.kitConfig = ConfigUtil.createConfig("kits");
        this.arenaConfig = ConfigUtil.createConfig("arenas");
        this.scoreboardConfig = ConfigUtil.createConfig("scoreboard");
        this.messagesConfig = ConfigUtil.createConfig("messages");
        this.setupListeners();
        this.setupManagers();
        this.setupCommands();
        this.setupTasks();
        this.setupGameRules();
        this.setupMongo();
    }

    @Override
    public void onDisable() {
        this.kitStorage.saveKits();
        this.arenaStorage.saveArenas();
        ConfigUtil.saveConfig(mainConfig);
        ConfigUtil.saveConfig(scoreboardConfig);
    }

    private void setupListeners() {
        for (Class<?> c : ClassUtils.getClasses(getFile(), "dev.lugami.practice.listeners")) {
            try {
                Bukkit.getPluginManager().registerEvents((Listener) c.newInstance(), this);
            } catch (Exception exception) {
                getLogger().info("Error while loading the listener " + c.getSimpleName());
            }
        }
    }

    private void setupManagers() {
        this.profileStorage = new ProfileStorage();
        this.lobbyStorage = new LobbyStorage();
        this.kitStorage = new KitStorage();
        this.arenaStorage = new ArenaStorage();
        this.matchStorage = new MatchStorage();
        this.hotbarStorage = new HotbarStorage();
        this.queueStorage = new QueueStorage();
        this.leaderboardsStorage = new LeaderboardsStorage();
        this.partyStorage = new PartyStorage();
        this.entityHider = new EntityHider();
        this.assemble = new Assemble(this, new ScoreboardProvider());
        this.assemble.setTicks(2);
        this.assemble.setAssembleStyle(AssembleStyle.MODERN);
    }

    private void setupCommands() {
        drink = Drink.get(this);
        for (Class<?> c : ClassUtils.getClasses(getFile(), "dev.lugami.practice.commands.impl")) {
            if (c.getName().contains("$")) continue;
            try {
                CommandBase command = (CommandBase) c.newInstance();
                drink.register(command, command.getName(), command.getAliases());
            } catch (Exception exception) {
                getLogger().info("Error while loading the command " + c.getSimpleName());
                exception.printStackTrace();
            }
        }
        drink.registerCommands();
    }

    private void setupTasks() {
        TaskUtil.runTaskTimerAsynchronously(new MatchSnapshotTask(), 0, 2);
        TaskUtil.runTaskTimerAsynchronously(new MatchEnderpearlTask(), 0, 2);
        TaskUtil.runTaskTimerAsynchronously(new QueueTask(), 0, 2);
        TaskUtil.runTaskTimer(() -> {
            for (Player p1 : Bukkit.getOnlinePlayers()) {
                if (Menu.getOpenMenus().containsKey(p1)) {
                    Menu menu = Menu.getOpenMenus().get(p1);
                    menu.updateButtonLore(p1);
                    menu.initialize(p1);
                    p1.updateInventory();
                }
            }
        }, 0, 20);
    }

    private void setupGameRules() {
        getServer().getWorlds().forEach(world -> {
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doDaylightCycle", "false");
        });
    }

    private void setupMongo() {
        if (mainConfig.getBoolean("mongo.auth.enabled")) {
            MongoCredential credential = MongoCredential.createCredential(
                    mainConfig.getString("mongo.auth.username"), "admin",
                    mainConfig.getString("mongo.auth.password").toCharArray());
            mongoDatabase = new MongoClient(new ServerAddress(mainConfig.getString("mongo.ip"), mainConfig.getInt("mongo.port")), credential, MongoClientOptions.builder().build()).getDatabase(mainConfig.getString("mongo.database"));
        } else {
            mongoDatabase = new MongoClient(mainConfig.getString("mongo.ip"), mainConfig.getInt("mongo.port")).getDatabase(mainConfig.getString("mongo.database"));
        }
    }
}
