package dev.lugami.practice.match.event;

import dev.lugami.practice.match.Match;
import dev.lugami.practice.match.team.Team;
import dev.lugami.practice.utils.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor @RequiredArgsConstructor
@Getter
public class MatchEndEvent extends BaseEvent {

    private final Match match;
    private final Team winner;
    private final Team loser;

    private Player winnerPlayer;
    private List<Player> losers;

}
