package dev.lugami.practice.profile;

import dev.lugami.practice.settings.Settings;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ProfileSettings {

    private final Map<Settings, Boolean> settingsMap;

    public ProfileSettings() {
        this.settingsMap = new HashMap<>();
        for (Settings settings : Settings.values()) {
            settingsMap.put(settings, settings.isDefaultToggled());
        }
    }

}
