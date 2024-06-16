package dev.lugami.practice;

import dev.lugami.practice.board.ScoreboardProvider;
import dev.lugami.practice.commands.CommandBase;
import dev.lugami.practice.storage.ArenaStorage;
import dev.lugami.practice.storage.KitStorage;
import dev.lugami.practice.storage.LobbyStorage;
import dev.lugami.practice.storage.ProfileStorage;
import dev.lugami.practice.utils.ClassUtils;
import dev.lugami.practice.utils.ConfigUtil;
import dev.lugami.practice.utils.command.Drink;
import dev.lugami.practice.utils.command.command.CommandService;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public class Budget extends JavaPlugin {

    @Getter
    private static Budget instance;
    private YamlConfiguration mainConfig, kitConfig, arenaConfig;
    private ProfileStorage profileStorage;
    private LobbyStorage lobbyStorage;
    private KitStorage kitStorage;
    private ArenaStorage arenaStorage;
    private Assemble assemble;
    private CommandService drink;

    @Override
    public void onEnable() {
        instance = this;
        this.mainConfig = ConfigUtil.createConfig("config");
        this.kitConfig = ConfigUtil.createConfig("kits");
        this.arenaConfig = ConfigUtil.createConfig("arenas");
        this.setupListeners();
        this.setupManagers();
        this.setupCommands();
    }

    @Override
    public void onDisable() {
        this.kitStorage.save();
        this.arenaStorage.save();
        ConfigUtil.saveConfig(mainConfig, "config");
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
        this.assemble = new Assemble(this, new ScoreboardProvider());
        this.assemble.setTicks(2);
        this.assemble.setAssembleStyle(AssembleStyle.MODERN);
    }

    private void setupCommands() {
        drink = Drink.get(this);
        for (Class<?> c : ClassUtils.getClasses(getFile(), "dev.lugami.practice.commands.impl")) {
            try {
                CommandBase command = (CommandBase) c.newInstance();
                drink.register(command, command.getName(), command.getAliases());
            } catch (Exception exception) {
                getLogger().info("Error while loading the command " + c.getSimpleName());
            }
        }
        drink.registerCommands();
    }
}
