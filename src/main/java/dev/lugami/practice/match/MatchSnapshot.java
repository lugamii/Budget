package dev.lugami.practice.match;

import dev.lugami.practice.Budget;
import dev.lugami.practice.utils.MathUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
public class MatchSnapshot {

    private final UUID id = UUID.randomUUID();
    private final Player target;
    private final Player opponent;
    private final Match match;
    private final double health;
    private final double hunger;
    private final int thrownPots;
    private final int missedPots;

    private final ItemStack[] armor;
    private final ItemStack[] contents;
    @Setter
    private boolean expired = false;
    private final long addedOn = System.currentTimeMillis();
    private final List<PotionEffect> effects;

    public MatchSnapshot(Player target, Player opponent, ItemStack[] armor, ItemStack[] contents) {
        this.target = target;
        this.opponent = opponent;
        this.armor = armor;
        this.contents = contents;
        this.health = MathUtils.roundHalf(target.getHealth());
        this.hunger = MathUtils.roundHalf(target.getFoodLevel());
        this.effects = new ArrayList<>(target.getActivePotionEffects());
        this.match = Budget.getInstance().getMatchStorage().findMatch(target.getUniqueId());
        if (this.match != null) {
            this.thrownPots = this.match.getTeam(target).getMember(target).getThrownPots();
            this.missedPots = this.match.getTeam(target).getMember(target).getMissedPots();
        } else {
            this.thrownPots = 0;
            this.missedPots = 0;
        }
    }

    public int getRemainingPots() {
        AtomicInteger amount = new AtomicInteger();

        Arrays.stream(this.contents).collect(Collectors.toList()).forEach(item -> {
            if (item != null && item.getType() == Material.POTION && item.getDurability() == 16421) {
                amount.getAndIncrement();
            }
        });

        return amount.get();
    }

    public double getPotionAccuracy() {
        if (this.thrownPots == 0 || this.missedPots == 0) return 100.0;
        else if (this.thrownPots == this.missedPots) return 50.0;
        return Math.round(100.0D - (((double) this.missedPots / (double) this.thrownPots) * 100.0D));
    }

}
