package dev.lugami.practice.match.types;

import dev.lugami.practice.Budget;
import dev.lugami.practice.Language;
import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.match.MatchSnapshot;
import dev.lugami.practice.match.Team;
import dev.lugami.practice.match.event.MatchEndEvent;
import dev.lugami.practice.match.event.MatchStartEvent;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.queue.QueueType;
import dev.lugami.practice.settings.Setting;
import dev.lugami.practice.utils.*;
import dev.lugami.practice.utils.fake.FakePlayer;
import dev.lugami.practice.utils.fake.FakePlayerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class DefaultMatch extends Match {

    public DefaultMatch(Kit kit, Arena arena) {
        super(kit, arena, QueueType.UNRANKED);
    }

    public DefaultMatch(Kit kit, Arena arena, boolean npcTesting) {
        super(kit, arena, npcTesting);
    }

    public DefaultMatch(Kit kit, Arena arena, QueueType queueType) {
        super(kit, arena, queueType);
    }

}
