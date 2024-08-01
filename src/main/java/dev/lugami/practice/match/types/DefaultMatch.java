package dev.lugami.practice.match.types;

import dev.lugami.practice.arena.Arena;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.Match;
import dev.lugami.practice.queue.QueueType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultMatch extends Match {

    public DefaultMatch(Kit kit, Arena arena) {
        super(kit, arena, QueueType.UNRANKED);
    }

    public DefaultMatch(Kit kit, Arena arena, boolean npcTesting) {
        super(kit, arena, npcTesting);
    }

    public DefaultMatch(Kit kit, Arena arena, QueueType queueType) {
        super(kit, arena, queueType);
    }

}
