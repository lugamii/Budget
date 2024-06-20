package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.utils.ConfigUtil;
import dev.lugami.practice.utils.LocationUtil;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ArenaStorage {

    private final List<Arena> arenas = new ArrayList<>();

    /**
     * Constructor that initializes the arena storage and loads arenas from the configuration.
     */
    public ArenaStorage() {
        loadArenas();
    }

    /**
     * Finds an arena by its name.
     *
     * @param name The name of the arena.
     * @return The found arena, or null if not found.
     */
    public Arena getByName(String name) {
        return this.arenas.stream().filter(kit -> kit.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Loads arenas from the configuration file.
     */
    public void loadArenas() {
        YamlConfiguration config = Budget.getInstance().getArenaConfig();
        for (String key : config.getKeys(false)) {
            String path = key + ".";
            Arena arena = new Arena(key);
            arena.setEnabled(config.getBoolean(path + "enabled"));
            arena.setPos1(LocationUtil.stringToLocation(config.getString(path + "pos1")));
            arena.setPos2(LocationUtil.stringToLocation(config.getString(path + "pos2")));
            arena.setMin(LocationUtil.stringToLocation(config.getString(path + "min")));
            arena.setMax(LocationUtil.stringToLocation(config.getString(path + "max")));
            arena.setWhitelistedKits(config.getStringList(path + "whitelistedKits"));
            arenas.add(arena);
        }
    }

    /**
     * Saves the current arenas to the configuration file.
     */
    public void saveArenas() {
        YamlConfiguration config = Budget.getInstance().getArenaConfig();
        for (Arena arena : this.arenas) {
            String path = arena.getName() + ".";
            config.set(path + "enabled", arena.isEnabled());
            config.set(path + "pos1", LocationUtil.locationToString(arena.getPos1()));
            config.set(path + "pos2", LocationUtil.locationToString(arena.getPos2()));
            config.set(path + "min", LocationUtil.locationToString(arena.getMin()));
            config.set(path + "max", LocationUtil.locationToString(arena.getMax()));
            config.set(path + "whitelistedKits", arena.getWhitelistedKits());
        }
        ConfigUtil.saveConfig(config, "arenas");
    }
}
