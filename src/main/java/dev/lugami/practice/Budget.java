package dev.lugami.practice;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import dev.lugami.practice.board.ScoreboardProvider;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.protocol.EntityHider;
import dev.lugami.practice.protocol.VersionFetcher;
import dev.lugami.practice.storage.*;
import dev.lugami.practice.utils.*;
import dev.lugami.practice.utils.command.Drink;
import dev.lugami.practice.utils.command.command.CommandService;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.AsyncCatcher;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
public class Budget extends JavaPlugin {

    @Getter
    private static Budget instance;
    private YamlConfiguration mainConfig, kitConfig, arenaConfig, scoreboardConfig, languageConfig;
    private ProfileStorage profileStorage;
    private LobbyStorage lobbyStorage;
    private KitStorage kitStorage;
    private ArenaStorage arenaStorage;
    private MatchStorage matchStorage;
    private HotbarStorage hotbarStorage;
    private QueueStorage queueStorage;
    private PartyStorage partyStorage;
    private LeaderboardsStorage leaderboardsStorage;
    private EditorStorage editorStorage;
    private Assemble assemble;
    private EntityHider entityHider;
    private VersionFetcher versionFetcher;
    private CommandService drink;
    private MongoDatabase mongoDatabase;


    /**
     * Lunar Client related stuff...
     */
    private boolean lunarHook = false;
    private String lunarHookMode = "None";

    @Override
    public void onEnable() {
        instance = this;
        this.mainConfig = ConfigUtil.createConfig("config");
        this.kitConfig = ConfigUtil.createConfig("kits");
        this.arenaConfig = ConfigUtil.createConfig("arenas");
        this.scoreboardConfig = ConfigUtil.createConfig("scoreboard");
        this.languageConfig = ConfigUtil.createConfig("language");
        this.setupListeners();
        this.setupManagers();
        this.setupCommands();
        this.setupTasks();
        this.setupGameRules();
        this.setupMongo();
        this.setupHooks();
        this.startupMessage();
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
        this.editorStorage = new EditorStorage();
        this.entityHider = new EntityHider();
        this.versionFetcher = new VersionFetcher();
        AsyncCatcher.enabled = false;
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
                if (drink.get(command.getName()) != null && !command.getName().equalsIgnoreCase("budget")) {
                    drink.get(command.getName()).setDefaultCommandIsHelp(true);
                }
            } catch (Exception exception) {
                getLogger().info("Error while loading the command " + c.getSimpleName());
                exception.printStackTrace();
            }
        }
        drink.registerCommands();
    }

    private void setupTasks() {
        for (Class<?> c : ClassUtils.getClasses(getFile(), "dev.lugami.practice.task")) {
            if (c.getName().contains("$")) continue;
            //if (c.getName().contains("AnimationTask") && !getMainConfig().getBoolean("TITLE.ANIMATION.ENABLED")) continue;
            try {
                CustomBukkitRunnable task = (CustomBukkitRunnable) c.newInstance();
                task.execute();
            } catch (Exception exception) {
                getLogger().info("Error while starting the task " + c.getSimpleName());
                exception.printStackTrace();
            }
        }
    }

    private void setupGameRules() {
        getServer().getWorlds().forEach(world -> {
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doDaylightCycle", "false");
        });
    }

    private void setupHooks() {
        if (Bukkit.getPluginManager().isPluginEnabled("LunarClientAPI")) {
            this.lunarHook = true;
            this.lunarHookMode = "Legacy";
        } else if (Bukkit.getPluginManager().isPluginEnabled("Apollo-Bukkit")) {
            this.lunarHook = true;
            this.lunarHookMode = "Modern";
        }
    }

    private void startupMessage() {
        Bukkit.getConsoleSender().sendMessage(CC.translate(CC.CHAT_BAR));
        List<String> logo = Arrays.asList(
                "  ____            _            _   ",
                " |  _ \\          | |          | |  ",
                " | |_) |_   _  __| | __ _  ___| |_ ",
                " |  _ <| | | |/ _` |/ _` |/ _ \\ __|",
                " | |_) | |_| | (_| | (_| |  __/ |_ ",
                " |____/ \\____|\\____|\\___ |\\___|\\__|",
                "                     __/ |         ",
                "                    |___/          "
        );
        logo.forEach(msg -> Bukkit.getConsoleSender().sendMessage(CC.translate("&6" + msg)));

        Bukkit.getConsoleSender().sendMessage(CC.translate(""));
        Bukkit.getConsoleSender().sendMessage(CC.translate("Budget was initialized successfully!"));
        Bukkit.getConsoleSender().sendMessage(CC.translate(""));
        Bukkit.getConsoleSender().sendMessage(CC.translate("Version: &6" + this.getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(CC.translate("Authors: &6" + this.getDescription().getAuthors()));
        Bukkit.getConsoleSender().sendMessage(CC.translate(""));
        Bukkit.getConsoleSender().sendMessage(CC.translate("Kits: &6" + this.kitStorage.getKits().size()));
        Bukkit.getConsoleSender().sendMessage(CC.translate("Arenas: &6" + this.arenaStorage.getArenas().size()));
        Bukkit.getConsoleSender().sendMessage(CC.translate("Spigot: &6" + this.getServer().getName()));
        Bukkit.getConsoleSender().sendMessage(CC.translate("Lunar Support: &6" + (this.lunarHook ? "Yes (" + this.lunarHookMode + ")" : "No")));

        Bukkit.getConsoleSender().sendMessage(CC.translate(CC.CHAT_BAR));
    }


    /**
     * Credits to Refine Development.
     */
    private void disableLogging() {
        Logger mongoLogger = Logger.getLogger("com.mongodb");
        mongoLogger.setLevel(Level.SEVERE);

        Logger legacyLogger = Logger.getLogger("org.mongodb");
        legacyLogger.setLevel(Level.SEVERE);
    }

    private void setupMongo() {
        this.disableLogging();
        if (this.mainConfig.getBoolean("mongo.auth.enabled")) {
            MongoCredential credential = MongoCredential.createCredential(
                    this.mainConfig.getString("mongo.auth.username"), "admin",
                    this.mainConfig.getString("mongo.auth.password").toCharArray());
            this.mongoDatabase = new MongoClient(new ServerAddress(this.mainConfig.getString("mongo.ip"), this.mainConfig.getInt("mongo.port")), credential, MongoClientOptions.builder().build()).getDatabase(this.mainConfig.getString("mongo.database"));
        } else {
            this.mongoDatabase = new MongoClient(this.mainConfig.getString("mongo.ip"), this.mainConfig.getInt("mongo.port")).getDatabase(this.mainConfig.getString("mongo.database"));
        }
    }
}
