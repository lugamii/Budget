package dev.lugami.practice.profile;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lugami.practice.Budget;
import dev.lugami.practice.settings.ProfileSettings;
import dev.lugami.practice.settings.Setting;
import dev.lugami.practice.utils.Cooldown;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class Profile {

    private static MongoCollection<Document> collection = Budget.getInstance().getMongoDatabase().getCollection("profiles");

    private final Player player;
    private final UUID UUID;
    private ProfileState state;
    private Cooldown enderpearlCooldown;
    private ProfileSettings profileOptions;

    public Profile(Player player) {
        this(player, player.getUniqueId());
    }

    public Profile(UUID uuid) {
        this(Bukkit.getPlayer(uuid), uuid);
    }

    public Profile(Player player, UUID uuid) {
        this.player = player;
        this.UUID = uuid;
        this.state = ProfileState.LOBBY;
        this.enderpearlCooldown = new Cooldown(0);
        this.profileOptions = new ProfileSettings(this);
        Budget.getInstance().getProfileStorage().getProfiles().add(this);
        load();
    }

    public void load() {
        Document document = collection.find(Filters.eq("uuid", this.UUID.toString())).first();

        if (document == null) {
            this.save();
        } else {
            Document options = (Document) document.get("options");
            this.profileOptions.getSettingsMap().put(Setting.SCOREBOARD, (boolean) options.getOrDefault("showScoreboard", Setting.SCOREBOARD.isDefaultToggled()));
            this.profileOptions.getSettingsMap().put(Setting.DUEL_REQUESTS, (boolean) options.getOrDefault("duelRequests", Setting.DUEL_REQUESTS.isDefaultToggled()));
            this.profileOptions.getSettingsMap().put(Setting.ARENA_SELECTOR, (boolean) options.getOrDefault("arenaSelector", Setting.ARENA_SELECTOR.isDefaultToggled()));
        }
    }

    public void save() {
        Document document = new Document();
        document.put("uuid", this.UUID.toString());

        Document optionsDocument = new Document();
        optionsDocument.put("showScoreboard", profileOptions.getSettingsMap().get(Setting.SCOREBOARD));
        optionsDocument.put("duelRequests", profileOptions.getSettingsMap().get(Setting.DUEL_REQUESTS));
        optionsDocument.put("arenaSelector", profileOptions.getSettingsMap().get(Setting.ARENA_SELECTOR));
        document.put("options", optionsDocument);

        collection.replaceOne(Filters.eq("uuid", this.UUID.toString()), document, new ReplaceOptions().upsert(true));
    }

    public boolean isBusy() {
        return state != ProfileState.LOBBY;
    }

    public boolean isFighting() {
        return state == ProfileState.FIGHTING;
    }

}
