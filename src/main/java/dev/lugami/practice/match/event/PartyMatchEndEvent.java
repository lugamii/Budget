package dev.lugami.practice.match.event;

import dev.lugami.practice.match.Match;
import dev.lugami.practice.utils.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
@Getter
public class PartyMatchEndEvent extends BaseEvent {

    private final Match match;
    private final Player winner;
    private final List<Player> losers;

}
