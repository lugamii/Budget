package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.utils.ConfigUtil;
import dev.lugami.practice.utils.LocationUtil;
import dev.lugami.practice.utils.cuboid.Cuboid;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class ArenaStorage {

    private final List<Arena> arenas = new CopyOnWriteArrayList<>();

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
     * Gets a random arena from the list.
     *
     * @return The found arena
     */
    public Arena getRandomArena() {
        return this.arenas.get(ThreadLocalRandom.current().nextInt(this.arenas.size()));
    }

    /**
     * Gets a random arena from the list, with a given kit.
     *
     * @param kit The kit to check for
     * @return The found arena
     */
    public Arena getRandomArena(Kit kit) {
        List<Arena> arenas1 = this.arenas.stream().filter(arena -> arena.getWhitelistedKits().contains(kit.getName())).collect(Collectors.toList());
        return arenas1.get(ThreadLocalRandom.current().nextInt(this.arenas.size()));
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
            arena.setCuboid(new Cuboid(arena.getMin(), arena.getMax()));
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
