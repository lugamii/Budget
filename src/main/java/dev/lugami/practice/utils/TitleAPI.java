package dev.lugami.practice.utils;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.TitleType;
import dev.lugami.practice.Budget;
import lombok.experimental.UtilityClass;
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
            LunarClientAPI.getInstance().sendTitle(player, TitleType.TITLE, title, Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut));
            LunarClientAPI.getInstance().sendTitle(player, TitleType.SUBTITLE, subtitle, Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut));
        }
    }

    public void resetTitle(Player player) {
        player.resetTitle();
        if (Budget.getInstance().isLunarHook()) {
            LunarClientAPI.getInstance().sendTitle(player, TitleType.TITLE, "", Duration.ofMillis(0), Duration.ofMillis(0), Duration.ofMillis(0));
            LunarClientAPI.getInstance().sendTitle(player, TitleType.SUBTITLE, "", Duration.ofMillis(0), Duration.ofMillis(0), Duration.ofMillis(0));
        }
    }

}
