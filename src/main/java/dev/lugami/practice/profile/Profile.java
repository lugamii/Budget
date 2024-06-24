package dev.lugami.practice.profile;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.settings.ProfileSettings;
import dev.lugami.practice.settings.Setting;
import dev.lugami.practice.utils.Cooldown;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class Profile {

    private static MongoCollection<Document> collection = Budget.getInstance().getMongoDatabase().getCollection("profiles");

    private final Player player;
    private final UUID UUID;
    private ProfileState state;
    private Cooldown enderpearlCooldown;
    private ProfileSettings profileOptions;
    private List<ProfileStatistics> kitStats;

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
        this.kitStats = new ArrayList<>();
        Budget.getInstance().getProfileStorage().getProfiles().add(this);
        load();
    }

    public void load() {
        Document document = collection.find(Filters.eq("uuid", this.UUID.toString())).first();

        if (document == null) {
            this.save();
        } else {
            try {
                Document options = (Document) document.get("options");
                this.profileOptions.getSettingsMap().put(Setting.SCOREBOARD, (boolean) options.getOrDefault("showScoreboard", Setting.SCOREBOARD.isDefaultToggled()));
                this.profileOptions.getSettingsMap().put(Setting.DUEL_REQUESTS, (boolean) options.getOrDefault("duelRequests", Setting.DUEL_REQUESTS.isDefaultToggled()));
                this.profileOptions.getSettingsMap().put(Setting.ARENA_SELECTOR, (boolean) options.getOrDefault("arenaSelector", Setting.ARENA_SELECTOR.isDefaultToggled()));
                Document kitStatistics = (Document) document.get("profileStatistics");
                for (String key : kitStatistics.keySet()) {
                    Document kitDoc = (Document) kitStatistics.get(key);
                    Kit kit = Budget.getInstance().getKitStorage().getByName(key);
                    if (kit != null) {
                        ProfileStatistics stats = new ProfileStatistics(kit);
                        stats.setElo(kitDoc.getInteger("elo"));
                        stats.setWon(kitDoc.getInteger("won"));
                        stats.setLost(kitDoc.getInteger("lost"));
                        this.kitStats.add(stats);
                    }
                }
            } catch (Exception ex) {
                this.save();
            }
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

        Document profileStatisticsDocument = new Document();
        if (this.kitStats.isEmpty()) Budget.getInstance().getKitStorage().getKits().forEach(kit -> this.kitStats.add(new ProfileStatistics(kit)));

        for (ProfileStatistics stats : this.kitStats) {
            Document kitDocument = new Document();
            kitDocument.put("elo", stats.getElo());
            kitDocument.put("won", stats.getWon());
            kitDocument.put("lost", stats.getLost());
            profileStatisticsDocument.put(stats.getKit().getName(), kitDocument);
        }

        document.put("profileStatistics", profileStatisticsDocument);

        collection.replaceOne(Filters.eq("uuid", this.UUID.toString()), document, new ReplaceOptions().upsert(true));
    }

    public boolean isBusy() {
        return state != ProfileState.LOBBY;
    }

    public boolean isFighting() {
        return state == ProfileState.FIGHTING;
    }

    public ProfileStatistics getStatistics(Kit kit) {
        return this.kitStats.stream().filter(stats -> stats.getKit().equals(kit)).findFirst().orElse(null);
    }

}
