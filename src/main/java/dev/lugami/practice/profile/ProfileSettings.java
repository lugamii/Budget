package dev.lugami.practice.profile;

import dev.lugami.practice.settings.Settings;
import dev.lugami.practice.settings.SettingsChangedEvent;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ProfileSettings {

    private final Profile profile;
    private final Map<Settings, Boolean> settingsMap;

    public ProfileSettings(Profile profile) {
        this.profile = profile;
        this.settingsMap = new HashMap<>();
        for (Settings settings : Settings.values()) {
            settingsMap.put(settings, settings.isDefaultToggled());
        }
    }

    public boolean isToggled(Settings settings) {
        return this.settingsMap.get(settings);
    }

    public void setToggled(Settings settings, boolean toggled) {
        this.settingsMap.put(settings, toggled);
        new SettingsChangedEvent(settings, toggled, this.profile.getPlayer()).call();
    }

}
