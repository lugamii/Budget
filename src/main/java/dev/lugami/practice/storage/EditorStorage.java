package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.profile.editor.EditingMetadata;
import dev.lugami.practice.utils.LocationUtil;
import dev.lugami.practice.utils.PlayerUtils;
import dev.lugami.practice.utils.TaskUtil;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Data
public class EditorStorage {

    private Location editorLocation;

    /**
     * Constructor that initializes the editor location from the configuration.
     */
    public EditorStorage() {
        this.editorLocation = LocationUtil.stringToLocation(Budget.getInstance().getMainConfig().getString("editorLocation"));
    }

    /**
     * Brings a player to the editor.
     *
     * @param player The player to bring to the editor.
     */
    public void bringToEditor(Player player, Kit kit) {
        TaskUtil.runTaskLater(() -> {
            Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
            profile.setState(ProfileState.EDITOR);
            profile.setMatchState(MatchPlayerState.NONE);
            EditingMetadata editingMetadata = new EditingMetadata();
            editingMetadata.setEditing(kit);
            profile.setEditingMetadata(editingMetadata);
            PlayerUtils.resetPlayer(player);
            Budget.getInstance().getHotbarStorage().resetHotbar(player);
            player.getInventory().clear();
            player.getInventory().setContents(kit.getInventory());
            player.getInventory().setArmorContents(kit.getArmor());
            for (int i = 0; i < 3; i++) {
                player.teleport(this.editorLocation);
            }
        }, 1L);
    }

}
