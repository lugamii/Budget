package dev.lugami.practice.task;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import dev.lugami.practice.Budget;
import dev.lugami.practice.utils.CustomBukkitRunnable;
import dev.lugami.practice.utils.NotificationAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class LunarWaypointTask extends CustomBukkitRunnable {

    public LunarWaypointTask() {
        super(Mode.TIMER, Type.ASYNC, 2, 0);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (Budget.getInstance().isLunarHook() && Budget.getInstance().getLunarHookMode().equalsIgnoreCase("Modern")) {
                Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(player.getUniqueId());
                WaypointModule waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);
                apolloPlayerOpt.ifPresent(apolloPlayer -> waypointModule.removeWaypoint(apolloPlayer, "Spawn"));
            }
        });
    }
}
