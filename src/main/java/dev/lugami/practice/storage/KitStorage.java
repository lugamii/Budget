package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.utils.ConfigUtil;
import dev.lugami.practice.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter
public class KitStorage {

    private final List<Kit> kits = new ArrayList<>();

    public KitStorage() {
        load();
    }

    public Kit getByName(String name) {
        return kits.stream().filter(kit -> kit.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void load() {
        YamlConfiguration config = Budget.getInstance().getKitConfig();
        for (String key : config.getKeys(false)) {
            String path = key + ".";
            Kit kit = new Kit(key);
            kit.setEnabled(config.getBoolean(path + "enabled"));
            kit.setIcon(ItemUtils.deserializeItemStack(config.getString(path + "icon")));
            kit.setInventory(ItemUtils.deserializeInventory(config.getString(path + "inventory")));
            kit.setArmor(ItemUtils.deserializeInventory(config.getString(path + "armor")));
            kits.add(kit);
        }
    }

    public void save() {
        YamlConfiguration config = Budget.getInstance().getKitConfig();
        for (Kit kit : this.kits) {
            String path = kit.getName() + ".";
            config.set(path + "enabled", kit.isEnabled());
            config.set(path + "icon", ItemUtils.serializeItemStack(kit.getIcon()));
            config.set(path + "inventory", ItemUtils.serializeInventory(kit.getInventory()));
            config.set(path + "armor", ItemUtils.serializeInventory(kit.getArmor()));
        }
        ConfigUtil.saveConfig(config, "kits");
    }

}
