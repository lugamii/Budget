package dev.lugami.practice.match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class MatchSnapshot {

    private final UUID id = UUID.randomUUID();
    private final Player target;
    private final Player opponent;
    private final ItemStack[] armor;
    private final ItemStack[] contents;
    @Setter
    private boolean expired = false;
    private final long addedOn = System.currentTimeMillis();

}
