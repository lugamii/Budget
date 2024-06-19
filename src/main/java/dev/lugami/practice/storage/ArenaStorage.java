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

    public ArenaStorage() {
        load();
    }

    public Arena getByName(String name) {
        return arenas.stream().filter(kit -> kit.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void load() {
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

    public void save() {
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