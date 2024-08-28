package dev.lugami.practice.arena;

import dev.lugami.practice.utils.cuboid.Cuboid;
import lombok.Data;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Data
public class Arena {

    private String name;
    private boolean enabled;
    private Location pos1;
    private Location pos2;
    private Location min;
    private Location max;
    private List<String> whitelistedKits = new ArrayList<>();
    private Cuboid cuboid;

    public Arena(String name) {
        this.name = name;
    }

    public void updateCuboid() {
        this.cuboid = new Cuboid(min, max);
    }

}
