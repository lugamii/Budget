package dev.lugami.practice.profile;

import dev.lugami.practice.kit.Kit;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ProfileStatistics {

    private final Kit kit;
    private int elo = 1000;
    private int won = 0;
    private int lost = 0;

    public ProfileStatistics(Kit kit) {
        this.kit = kit;
    }

}
