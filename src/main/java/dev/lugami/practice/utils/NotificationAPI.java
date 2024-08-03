package dev.lugami.practice.utils;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.notification.Notification;
import com.lunarclient.apollo.module.notification.NotificationModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import dev.lugami.practice.Budget;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

/**
 * This is a API for Apollo's notification system.
 * Only supports Lunar Client currently.
 */
@UtilityClass
public class NotificationAPI {

    NotificationModule notificationModule = Apollo.getModuleManager().getModule(NotificationModule.class);

    public void showNotification(Player player, int duration, String title, String... message) {
        title = CC.translate(title);
        if (Budget.getInstance().isLunarHook() && Budget.getInstance().getLunarHookMode().equalsIgnoreCase("Modern")) {
            Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(player.getUniqueId());
            Component component = Component.text(String.join("\n", CC.translate(message)));
            Notification notification = Notification.builder()
                    .titleComponent(Component.text(title))
                    .descriptionComponent(component)
                    .displayTime(Duration.ofSeconds(duration))
                    .build();
            apolloPlayerOpt.ifPresent(apolloPlayer -> notificationModule.displayNotification(apolloPlayer, notification));
        } else {
            Budget.getInstance().getLogger().warning("Tried to display a Apollo notification for " + player.getName() + ", but Apollo was not found. Returning...");
        }
    }

    public void resetNotification(Player player) {
        if (Budget.getInstance().isLunarHook() && Budget.getInstance().getLunarHookMode().equalsIgnoreCase("Modern")) {
            Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(player.getUniqueId());
            apolloPlayerOpt.ifPresent(apolloPlayer -> notificationModule.resetNotifications(apolloPlayer));
        } else {
            Budget.getInstance().getLogger().warning("Tried to display a Apollo notification for " + player.getName() + ", but Apollo was not found. Returning...");
        }
    }
}
