package dev.lugami.practice.profile;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lugami.practice.Budget;
import dev.lugami.practice.hotbar.HotbarItem;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.match.MatchPlayerState;
import dev.lugami.practice.party.Party;
import dev.lugami.practice.profile.editor.CustomKitLayout;
import dev.lugami.practice.profile.editor.EditingMetadata;
import dev.lugami.practice.profile.misc.DiscMetadata;
import dev.lugami.practice.settings.Settings;
import dev.lugami.practice.utils.*;
import dev.lugami.practice.utils.fake.FakePlayer;
import lombok.Data;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

@Data
public class Profile {

    @Getter
    private static MongoCollection<Document> collection = Budget.getInstance().getMongoDatabase().getCollection("profiles");

    private final Player player;
    private final UUID UUID;
    private ProfileState state;
    private MatchPlayerState matchState;
    private Cooldown enderpearlCooldown;
    private ProfileSettings profileOptions;
    private List<ProfileStatistics> kitStats;
    private Party party = null;
    private Map<Kit, CustomKitLayout[]> kitLayouts;
    private EditingMetadata editingMetadata;
    private DiscMetadata discMetadata;

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
        this.matchState = MatchPlayerState.NONE;
        this.enderpearlCooldown = new Cooldown(0);
        this.profileOptions = new ProfileSettings(this);
        this.kitStats = new ArrayList<>();
        this.kitLayouts = new HashMap<>();
        this.editingMetadata = null;
        this.discMetadata = new DiscMetadata(this, null);
        Budget.getInstance().getProfileStorage().getProfiles().add(this);
        load();
    }

    // Please, only use this method if you're testing stuff with FakePlayersUtils#spawnFakePlayer
    public Profile(FakePlayer craftPlayer, boolean verify) {
        this.player = craftPlayer;
        this.UUID = craftPlayer.getUniqueId();
        this.state = ProfileState.LOBBY;
        this.matchState = MatchPlayerState.NONE;
        this.enderpearlCooldown = new Cooldown(0);
        this.profileOptions = new ProfileSettings(this);
        this.kitStats = new ArrayList<>();
        this.kitLayouts = new HashMap<>();
        this.editingMetadata = null;
        this.discMetadata = new DiscMetadata(this, null);
        Budget.getInstance().getProfileStorage().getProfiles().add(this);
    }

    public void load() {
        Document document = collection.find(Filters.eq("uuid", this.UUID.toString())).first();

        if (document == null) {
            this.save();
        } else {
            try {
                Document options = (Document) document.get("options");
                this.profileOptions.getSettingsMap().put(Settings.SCOREBOARD, (boolean) options.getOrDefault("showScoreboard", Settings.SCOREBOARD.isDefaultToggled()));
                this.profileOptions.getSettingsMap().put(Settings.DUEL_REQUESTS, (boolean) options.getOrDefault("duelRequests", Settings.DUEL_REQUESTS.isDefaultToggled()));
                this.profileOptions.getSettingsMap().put(Settings.PARTY_REQUESTS, (boolean) options.getOrDefault("partyRequests", Settings.PARTY_REQUESTS.isDefaultToggled()));
                this.profileOptions.getSettingsMap().put(Settings.ARENA_SELECTOR, (boolean) options.getOrDefault("arenaSelector", Settings.ARENA_SELECTOR.isDefaultToggled()));
                this.profileOptions.getSettingsMap().put(Settings.ALLOW_SPECTATORS, (boolean) options.getOrDefault("allowSpectators", Settings.ALLOW_SPECTATORS.isDefaultToggled()));
                this.profileOptions.getSettingsMap().put(Settings.SILENT_SPECTATE, (boolean) options.getOrDefault("silentSpectate", Settings.SILENT_SPECTATE.isDefaultToggled()));
                this.profileOptions.getSettingsMap().put(Settings.LOBBY_MUSIC, (boolean) options.getOrDefault("lobbyMusic", Settings.LOBBY_MUSIC.isDefaultToggled()));
                this.profileOptions.getSettingsMap().put(Settings.LIGHTNING, (boolean) options.getOrDefault("lightningEffect", Settings.LIGHTNING.isDefaultToggled()));
                this.profileOptions.getSettingsMap().put(Settings.EXPLOSION, (boolean) options.getOrDefault("explosionEffect", Settings.EXPLOSION.isDefaultToggled()));

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

                for (Kit kit : Budget.getInstance().getKitStorage().getKits()) {
                    CustomKitLayout[] list = new CustomKitLayout[4];
                    Arrays.fill(list, null);
                    this.kitLayouts.put(kit, list);
                }
                Document loadouts = (Document) document.get("loadouts");
                Budget.getInstance().getKitStorage().getKits().forEach(kit -> {
                    List<Document> layoutDocuments = (List<Document>) loadouts.get(kit.getName());
                    if (layoutDocuments != null) {
                        CustomKitLayout[] layouts = new CustomKitLayout[4];
                        int index = 0;
                        for (Document layoutDocument : layoutDocuments) {
                            CustomKitLayout layout = new CustomKitLayout();
                            layout.setInventory(ItemUtils.deserializeInventory(layoutDocument.getString("inventory")));
                            layout.setArmor(ItemUtils.deserializeInventory(layoutDocument.getString("armor")));
                            layouts[index++] = layout;
                        }
                        this.kitLayouts.put(kit, layouts);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                player.kickPlayer(CC.translate("&cFailed to load your profile."));
                this.save();
            }
        }
    }

    public void save() {
        Document document = new Document();
        document.put("uuid", this.UUID.toString());

        Document optionsDocument = new Document();
        optionsDocument.put("showScoreboard", this.profileOptions.getSettingsMap().get(Settings.SCOREBOARD));
        optionsDocument.put("duelRequests", this.profileOptions.getSettingsMap().get(Settings.DUEL_REQUESTS));
        optionsDocument.put("partyRequests", this.profileOptions.getSettingsMap().get(Settings.PARTY_REQUESTS));
        optionsDocument.put("arenaSelector", this.profileOptions.getSettingsMap().get(Settings.ARENA_SELECTOR));
        optionsDocument.put("allowSpectators", this.profileOptions.getSettingsMap().get(Settings.ALLOW_SPECTATORS));
        optionsDocument.put("silentSpectate", this.profileOptions.getSettingsMap().get(Settings.SILENT_SPECTATE));
        optionsDocument.put("lobbyMusic", this.profileOptions.getSettingsMap().get(Settings.LOBBY_MUSIC));
        optionsDocument.put("lightningEffect", this.profileOptions.getSettingsMap().get(Settings.LIGHTNING));
        optionsDocument.put("explosionEffect", this.profileOptions.getSettingsMap().get(Settings.EXPLOSION));

        document.put("options", optionsDocument);

        Document profileStatisticsDocument = new Document();
        if (this.kitStats.isEmpty())
            Budget.getInstance().getKitStorage().getKits().forEach(kit -> this.kitStats.add(new ProfileStatistics(kit)));

        for (ProfileStatistics stats : this.kitStats) {
            Document kitDocument = new Document();
            kitDocument.put("elo", stats.getElo());
            kitDocument.put("won", stats.getWon());
            kitDocument.put("lost", stats.getLost());
            profileStatisticsDocument.put(stats.getKit().getName(), kitDocument);
        }

        document.put("profileStatistics", profileStatisticsDocument);

        Document kitLoadoutsDocument = new Document();
        Budget.getInstance().getKitStorage().getKits().forEach(kit -> {
            CustomKitLayout[] layouts = this.kitLayouts.get(kit);

            if (layouts != null) {
                List<Document> layoutDocuments = new ArrayList<>();
                boolean hasNonEmptyLayout = false;
                for (CustomKitLayout layout : layouts) {
                    if (layout != null) {
                        hasNonEmptyLayout = true;
                        Document layoutDoc = new Document("inventory", ItemUtils.serializeInventory(layout.getInventory()))
                                .append("armor", ItemUtils.serializeInventory(layout.getArmor()));
                        layoutDocuments.add(layoutDoc);
                    }
                }

                if (hasNonEmptyLayout) {
                    kitLoadoutsDocument.put(kit.getName(), layoutDocuments);
                }
            }
        });

        document.put("loadouts", kitLoadoutsDocument);

        collection.replaceOne(Filters.eq("uuid", this.UUID.toString()), document, new ReplaceOptions().upsert(true));
    }

    public List<HotbarItem> getKitItems(Kit kit) {
        List<HotbarItem> hotbarItems = new ArrayList<>();
        List<CustomKitLayout> layouts = Arrays.asList(getKitLayouts().get(kit));

        InventoryWrapper wrapper = new InventoryWrapper(player.getInventory());
        HotbarItem defaultKitItem = new HotbarItem(
                new ItemBuilder(Material.BOOK)
                        .name("&bDefault Kit")
                        .build(),
                player1 -> {
                    wrapper.clear();
                    wrapper.setContents(kit.getInventory());
                    wrapper.setArmorContents(kit.getArmor());
                    player.updateInventory();
                }
        );

        hotbarItems.add(defaultKitItem);

        if (layouts.isEmpty()) {
            Budget.getInstance().getLogger().warning("Empty layout for " + player.getName() + " on " + kit);
            return hotbarItems;
        }

        for (int i = 0; i < layouts.size(); i++) {
            CustomKitLayout layout = layouts.get(i);
            if (layout == null || layout.getInventory() == null || layout.getArmor() == null || (layout.getInventory() == kit.getInventory() && layout.getArmor() == kit.getArmor())) {
                continue;
            }
            HotbarItem hotbarItem = new HotbarItem(
                    new ItemBuilder(Material.BOOK)
                            .name("&bKit " + (i + 1))
                            .build(),
                    player1 -> {
                        wrapper.clear();
                        wrapper.setContents(layout.getInventory());
                        wrapper.setArmorContents(layout.getArmor());
                        player.updateInventory();
                    }
            );
            hotbarItems.add(hotbarItem);
        }

        return hotbarItems;
    }

    public void stopSong() {
        if (this.discMetadata != null) {
            this.discMetadata.setDisc(null);
            this.discMetadata.setFinished(true);
        }
    }

    public boolean isBusy() {
        return this.state != ProfileState.LOBBY && this.state != ProfileState.LOBBY_SPECTATE;
    }

    public boolean isAtSpawn() {
        return this.state == ProfileState.LOBBY || this.state == ProfileState.QUEUEING || this.state == ProfileState.LOBBY_SPECTATE;
    }

    public boolean isFighting() {
        return this.state == ProfileState.FIGHTING || Budget.getInstance().getMatchStorage().findMatch(player) != null;
    }

    public boolean isInParty() {
        return this.state == ProfileState.PARTY || this.party != null;
    }

    public boolean canJoinParties() {
        return !isInParty() && this.profileOptions.getSettingsMap().get(Settings.PARTY_REQUESTS);
    }

    public ProfileStatistics getStatistics(Kit kit) {
        return this.kitStats.stream().filter(stats -> stats.getKit().equals(kit)).findFirst().orElse(null);
    }

}
