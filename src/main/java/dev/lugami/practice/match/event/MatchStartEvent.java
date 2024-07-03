package dev.lugami.practice.match.event;

import dev.lugami.practice.match.types.DefaultMatch;
import dev.lugami.practice.match.Team;
import dev.lugami.practice.utils.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class MatchStartEvent extends BaseEvent {

    private final DefaultMatch match;
    private final Team team1;
    private final Team team2;


}
