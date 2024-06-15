package dev.lugami.practice.board;

import dev.lugami.practice.utils.CC;
import io.github.thatkawaiisam.assemble.AssembleAdapter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardProvider implements AssembleAdapter {
    @Override
    public String getTitle(Player player) {
        return "&6&lPractice";
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();
        lines.add(CC.SCORE_BAR);
        lines.add("&6Scoreboard test");
        lines.add("");
        lines.add("&6www.angolanos.fun");
        lines.add(CC.SCORE_BAR);
        return lines;
    }
}
