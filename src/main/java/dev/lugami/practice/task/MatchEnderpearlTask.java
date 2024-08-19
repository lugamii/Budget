package dev.lugami.practice.task;

import dev.lugami.practice.Budget;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.profile.ProfileState;
import dev.lugami.practice.utils.CC;
import dev.lugami.practice.utils.Cooldown;
import dev.lugami.practice.utils.CustomBukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchEnderpearlTask extends CustomBukkitRunnable {

    public MatchEnderpearlTask() {
        super(Mode.TIMER, Type.ASYNC, 2, 0);
    }

    @Override
    public void run() {
        for (Player player : Budget.getInstance().getServer().getOnlinePlayers()) {
            try {
                Profile profile = Budget.getInstance().getProfileStorage().findProfile(player);
                if (profile.getState() == ProfileState.FIGHTING) {
                    handleFightingPlayer(player, profile);
                } else {
                    resetPlayerCooldownDisplay(player);
                }
            } catch (Exception ex) {
                return;
            }
        }
    }

    private void handleFightingPlayer(Player player, Profile profile) {
        Cooldown enderpearlCooldown = profile.getEnderpearlCooldown();

        if (enderpearlCooldown.hasExpired()) {
            if (!enderpearlCooldown.isNotified()) {
                enderpearlCooldown.setNotified(true);
                player.sendMessage(CC.translate("&aYour enderpearl cooldown has expired."));
            }
        } else {
            updatePlayerCooldownDisplay(player, enderpearlCooldown);
        }
    }

    private void updatePlayerCooldownDisplay(Player player, Cooldown cooldown) {
        int seconds = (int) (cooldown.getRemaining() / 1_000);
        player.setLevel(seconds);
        player.setExp((float) cooldown.getRemaining() / 16_000.0F);
    }

    private void resetPlayerCooldownDisplay(Player player) {
        if (player.getLevel() > 0) {
            player.setLevel(0);
        }

        if (player.getExp() > 0.0F) {
            player.setExp(0.0F);
        }
    }

}
