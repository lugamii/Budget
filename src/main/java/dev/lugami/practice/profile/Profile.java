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
import dev.lugami.practice.settings.ProfileSettings;
import dev.lugami.practice.settings.Setting;
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
        this.profileOptions = new ProfileSettings();
        this.kitStats = new ArrayList<>();
        this.kitLayouts = new HashMap<>();
        this.editingMetadata = null;
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
        this.profileOptions = new ProfileSettings();
        this.kitStats = new ArrayList<>();
        this.kitLayouts = new HashMap<>();
        this.editingMetadata = null;
        Budget.getInstance().getProfileStorage().getProfiles().add(this);
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
                this.profileOptions.getSettingsMap().put(Setting.ALLOW_SPECTATORS, (boolean) options.getOrDefault("allowSpectators", Setting.ALLOW_SPECTATORS.isDefaultToggled()));
                this.profileOptions.getSettingsMap().put(Setting.SILENT_SPECTATE, (boolean) options.getOrDefault("silentSpectate", Setting.SILENT_SPECTATE.isDefaultToggled()));
                this.profileOptions.getSettingsMap().put(Setting.LIGHTNING, (boolean) options.getOrDefault("lightningEffect", Setting.LIGHTNING.isDefaultToggled()));
                this.profileOptions.getSettingsMap().put(Setting.EXPLOSION, (boolean) options.getOrDefault("explosionEffect", Setting.EXPLOSION.isDefaultToggled()));

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
        optionsDocument.put("showScoreboard", this.profileOptions.getSettingsMap().get(Setting.SCOREBOARD));
        optionsDocument.put("duelRequests", this.profileOptions.getSettingsMap().get(Setting.DUEL_REQUESTS));
        optionsDocument.put("arenaSelector", this.profileOptions.getSettingsMap().get(Setting.ARENA_SELECTOR));
        optionsDocument.put("allowSpectators", this.profileOptions.getSettingsMap().get(Setting.ALLOW_SPECTATORS));
        optionsDocument.put("silentSpectate", this.profileOptions.getSettingsMap().get(Setting.SILENT_SPECTATE));
        optionsDocument.put("lightningEffect", this.profileOptions.getSettingsMap().get(Setting.LIGHTNING));
        optionsDocument.put("explosionEffect", this.profileOptions.getSettingsMap().get(Setting.EXPLOSION));

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
                new ItemBuilder(Material.ENCHANTED_BOOK)
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
            HotbarItem hotbarItem = new HotbarItem(
                    new ItemBuilder(Material.ENCHANTED_BOOK)
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

        for (int i = hotbarItems.size(); i < 8; i++) {
            hotbarItems.add(new HotbarItem(new ItemBuilder(Material.AIR).build()));
        }

        return hotbarItems;
    }

    public boolean isBusy() {
        return this.state != ProfileState.LOBBY && this.state != ProfileState.SPECTATE_MODE;
    }

    public boolean isAtSpawn() {
        return this.state == ProfileState.LOBBY || this.state == ProfileState.QUEUEING || this.state == ProfileState.SPECTATE_MODE;
    }

    public boolean isFighting() {
        return this.state == ProfileState.FIGHTING;
    }

    public boolean isInParty() {
        return this.state == ProfileState.PARTY || this.party != null;
    }

    public ProfileStatistics getStatistics(Kit kit) {
        return this.kitStats.stream().filter(stats -> stats.getKit().equals(kit)).findFirst().orElse(null);
    }

}
