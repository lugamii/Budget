package dev.lugami.practice.settings;

import dev.lugami.practice.profile.Profile;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ProfileSettings {

    private final Map<Setting, Boolean> settingsMap;

    public ProfileSettings() {
        this.settingsMap = new HashMap<>();
        for (Setting setting : Setting.values()) {
            settingsMap.put(setting, setting.isDefaultToggled());
        }
    }

}
