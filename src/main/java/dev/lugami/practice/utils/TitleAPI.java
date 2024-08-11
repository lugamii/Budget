package dev.lugami.practice.utils;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.title.Title;
import com.lunarclient.apollo.module.title.TitleModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.TitleType;
import dev.lugami.practice.Budget;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;

@UtilityClass
public class TitleAPI {

    public void sendTitle(Player player, String title, String subtitle) {
        sendTitle(player, CC.translate(title), CC.translate(subtitle), 200, 1000, 500);
    }

    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        title = CC.translate(title);
        subtitle = CC.translate(subtitle);
        player.sendTitle(new org.github.paperspigot.Title(title, subtitle, fadeIn, stay, fadeOut));

        if (Budget.getInstance().isLunarHook()) {
            if (Budget.getInstance().getLunarHookMode().equalsIgnoreCase("Legacy")) {
                LunarClientAPI.getInstance().sendTitle(player, TitleType.TITLE, title, Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut));
                LunarClientAPI.getInstance().sendTitle(player, TitleType.SUBTITLE, subtitle, Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut));
            } else if (Budget.getInstance().getLunarHookMode().equalsIgnoreCase("Modern")) {
                Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(player.getUniqueId());
                TitleModule titleModule = Apollo.getModuleManager().getModule(TitleModule.class);
                Title t = Title.builder()
                        .type(com.lunarclient.apollo.module.title.TitleType.TITLE)
                        .message(Component.text().content(title).build())
                        .scale(1.0f)
                        .displayTime(Duration.ofMillis(stay))
                        .fadeInTime(Duration.ofMillis(fadeIn))
                        .fadeOutTime(Duration.ofMillis(fadeOut))
                        .build();
                Title s = Title.builder()
                        .type(com.lunarclient.apollo.module.title.TitleType.SUBTITLE)
                        .message(Component.text().content(subtitle).build())
                        .scale(1.0f)
                        .displayTime(Duration.ofMillis(stay))
                        .fadeInTime(Duration.ofMillis(fadeIn))
                        .fadeOutTime(Duration.ofMillis(fadeOut))
                        .build();
                apolloPlayerOpt.ifPresent(apolloPlayer -> titleModule.displayTitle(apolloPlayer, t));
                apolloPlayerOpt.ifPresent(apolloPlayer -> titleModule.displayTitle(apolloPlayer, s));
            }
        }
    }

    public void resetTitle(Player player) {
        player.resetTitle();
        if (Budget.getInstance().isLunarHook()) {
            if (Budget.getInstance().getLunarHookMode().equalsIgnoreCase("Legacy")) {
                LunarClientAPI.getInstance().sendTitle(player, TitleType.TITLE, "", Duration.ofMillis(0), Duration.ofMillis(0), Duration.ofMillis(0));
                LunarClientAPI.getInstance().sendTitle(player, TitleType.SUBTITLE, "", Duration.ofMillis(0), Duration.ofMillis(0), Duration.ofMillis(0));
            } else if (Budget.getInstance().getLunarHookMode().equalsIgnoreCase("Modern")) {
                Optional<ApolloPlayer> apolloPlayerOpt = Apollo.getPlayerManager().getPlayer(player.getUniqueId());
                TitleModule titleModule = Apollo.getModuleManager().getModule(TitleModule.class);
                apolloPlayerOpt.ifPresent(titleModule::resetTitles);
            }
        }
    }

}