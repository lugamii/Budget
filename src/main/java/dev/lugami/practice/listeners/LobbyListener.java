package dev.lugami.practice.listeners;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.notification.NotificationModule;
import com.lunarclient.apollo.module.title.TitleModule;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import dev.lugami.practice.Budget;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.NotificationAPI;
import dev.lugami.practice.utils.TaskUtil;
import dev.lugami.practice.utils.TitleAPI;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class LobbyListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
        if (profile.isAtSpawn() || profile.getState() == ProfileState.PARTY || profile.getState() == ProfileState.EDITOR) {
            event.setCancelled(true);
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                Budget.getInstance().getLobbyStorage().bringToLobby(player);
            }
        } else if (profile.isFighting()) {
            if (profile.getMatchState() == MatchPlayerState.DEAD) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Budget.getInstance().getLobbyStorage().bringToLobby(player);
        TaskUtil.runTaskLater(() -> {
            if (Budget.getInstance().getMainConfig().getBoolean("motd.clear-chat")) {
                for (int i = 0; i < 100; i++) player.sendMessage("");
            }
            if (Budget.getInstance().getMainConfig().getBoolean("motd.enabled")) {
                for (String s : Budget.getInstance().getMainConfig().getStringList("motd.message")) {
                    player.sendMessage(CC.translate(s));
                }
            }
            YamlConfiguration config = Budget.getInstance().getLanguageConfig();
            if (Budget.getInstance().isLunarHook() && Budget.getInstance().getLunarHookMode().equalsIgnoreCase("Modern")) {
                Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(player.getUniqueId());

                // Removes the default "Spawn" waypoint (which in my opinion is ugly asf)
                WaypointModule waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);
                apolloPlayerOpt.ifPresent(apolloPlayer -> waypointModule.removeWaypoint(apolloPlayer, "Spawn"));

                if (config.getBoolean("LUNAR-NOTIFICATIONS.JOIN.ENABLED")) NotificationAPI.showNotification(player, config.getInt("LUNAR-NOTIFICATIONS.JOIN.DURATION"), config.getString("LUNAR-NOTIFICATIONS.JOIN.TITLE"), config.getStringList("LUNAR-NOTIFICATIONS.JOIN.LINES").toArray(new String[0]));
            }

            if (config.getBoolean("TITLES.JOIN.ENABLED"))
                TitleAPI.sendTitle(player, config.getString("TITLES.JOIN.TITLE"), config.getString("TITLES.JOIN.SUBTITLE"), config.getInt("TITLES.JOIN.FADE-IN"), config.getInt("TITLES.JOIN.STAY"), config.getInt("TITLES.JOIN.FADE-OUT"));
        }, 1L);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !event.getPlayer().isOp()) {
            event.setCancelled(true);
        } else if (event.getPlayer().getGameMode() == GameMode.CREATIVE && event.getPlayer().isOp()) {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(event.getPlayer());
            event.setCancelled(profile.getState() != ProfileState.LOBBY);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasBlock()) {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(event.getPlayer());
            if (profile.isFighting()) {
                return;
            }
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !event.getPlayer().isOp()) {
                event.setCancelled(true);
            } else if (event.getPlayer().getGameMode() == GameMode.CREATIVE && event.getPlayer().isOp()) {
                event.setCancelled(profile.getState() != ProfileState.LOBBY);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !event.getPlayer().isOp()) {
            event.setCancelled(true);
        } else if (event.getPlayer().getGameMode() == GameMode.CREATIVE && event.getPlayer().isOp()) {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(event.getPlayer());
            event.setCancelled(profile.getState() != ProfileState.LOBBY);
        }
    }

}
