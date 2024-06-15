package dev.lugami.practice.utils;

import dev.lugami.practice.Budget;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@UtilityClass
public class ConfigUtil {

    private final File pluginDataFolder = Budget.getInstance().getDataFolder();

    /**
     * Creates a new configuration file with the given name.
     * If the file already exists, it will not be overwritten.
     *
     * @param name the name of the configuration file (without .yml extension)
     * @return the YamlConfiguration object
     */
    public YamlConfiguration createConfig(String name) {
        File configFile = new File(pluginDataFolder, name + ".yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            try (InputStream resourceStream = Budget.getInstance().getResource(name + ".yml")) {
                if (resourceStream != null) {
                    // Copy resource content to config file
                    Files.copy(resourceStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    configFile.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return loadConfig(name);
    }

    /**
     * Loads an existing configuration file.
     *
     * @param name the name of the configuration file (without .yml extension)
     * @return the YamlConfiguration object
     */
    public YamlConfiguration loadConfig(String name) {
        File configFile = new File(pluginDataFolder, name + ".yml");
        if (!configFile.exists()) {
            return null;
        }
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            saveConfig(config, name);
        }
        return config;
    }

    /**
     * Saves the given YamlConfiguration to a file with the given name.
     *
     * @param config the YamlConfiguration object
     * @param name   the name of the configuration file (without .yml extension)
     */
    public void saveConfig(YamlConfiguration config, String name) {
        File configFile = new File(pluginDataFolder, name + ".yml");
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the configuration file with the given name.
     *
     * @param name the name of the configuration file (without .yml extension)
     * @return true if the file was successfully deleted, false otherwise
     */
    public boolean deleteConfig(String name) {
        File configFile = new File(pluginDataFolder, name + ".yml");
        return configFile.delete();
    }
}
