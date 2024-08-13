package dev.lugami.practice.settings;

import dev.lugami.practice.utils.event.BaseEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor @Getter
public class SettingsChangedEvent extends BaseEvent {

    private final Settings settings;
    private final boolean toggled;
    private final Player player;

}
