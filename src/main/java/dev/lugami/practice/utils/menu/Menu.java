package dev.lugami.practice.utils.menu;

import dev.lugami.practice.Budget;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.TaskUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

@Getter
public class Menu {

    private final String title;
    private final int size;
    private final Inventory inventory;
    private final Map<Integer, Button> buttons;

    public Menu(String t, int s) {
        this.title = CC.translate(t);
        this.size = s;
        this.inventory = Bukkit.createInventory(null, size, title);
        this.buttons = new HashMap<>();
    }

    public void setButton(int slot, Button button) {
        buttons.put(slot, button);
        inventory.setItem(slot, button.getItemStack());
    }

    protected static void staticSetButton(Menu menu, int slot, Button button) {
        menu.setButton(slot, button);
    }

    public void open(Player player) {
        player.openInventory(inventory);
        TaskUtil.runTaskLater(() -> {
            if (Budget.getInstance().getMainConfig().getBoolean("debug")) {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    if (!player1.isOp()) return;
                    ArrayList<String> list = new ArrayList<>();
                    MenuManager.getMenus().forEach(m -> list.add(m.getTitle() + "&7&o"));
                    player1.sendMessage(CC.translate("&7&oDEBUG: Menu List: " + list));
                }
            }
        }, 10L);
    }

    public void handleClick(int slot, Player player) {
        if (buttons.containsKey(slot)) {
            Button button = buttons.get(slot);
            button.getAction().execute(player);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        return size == menu.size &&
               Objects.equals(title, menu.title) &&
               Objects.equals(inventory.getTitle(), menu.inventory.getTitle()) &&
               Objects.equals(inventory.getContents(), menu.inventory.getContents()) &&
               Objects.equals(buttons, menu.buttons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, inventory.getTitle(), inventory.getContents(), buttons, size);
    }
}
