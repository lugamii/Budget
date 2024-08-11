package dev.lugami.practice.task;

import dev.lugami.practice.Budget;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.match.types.PartyMatch;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchGeneralTask extends BukkitRunnable {

    @Override
    public void run() {
        Budget.getInstance().getMatchStorage().getRunningMatches().forEach(match -> {
            if (match.isPartyMatch()) {
                PartyMatch match1 = (PartyMatch) match;
                if (match1.getType() == PartyMatch.MatchType.FFA && match1.getAlive() == 1) {
                    for (Player player : match1.getAllPlayers()) {
                        if (Budget.getInstance().getProfileStorage().findProfile(player.getUniqueId()).getMatchState() == MatchPlayerState.ALIVE) {
                            match1.end(player);
                            break;
                        }
                    }
                }
            }
        });
    }

}
