package dev.lugami.practice.storage;

import dev.lugami.practice.Budget;
import dev.lugami.practice.kit.Kit;
import dev.lugami.practice.leaderboards.LeaderboardsEntry;
import dev.lugami.practice.profile.Profile;
import dev.lugami.practice.utils.TaskUtil;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class LeaderboardsStorage {

    @Getter
    private static Map<Kit, List<LeaderboardsEntry>> kitLeaderboards = new HashMap<>();

    public LeaderboardsStorage() {
        this.updateLeaderboards();
        TaskUtil.runTaskTimerAsynchronously(this::updateLeaderboards, 0L, 20L * 20L);
    }

    public void updateLeaderboards() {
        TaskUtil.runTaskAsynchronously(() -> {
            Map<Kit, List<LeaderboardsEntry>> updated = new HashMap<>();
            Budget.getInstance().getKitStorage().getKits().stream().filter(Kit::isRanked).collect(Collectors.toList()).forEach(kit -> {
                List<Document> sortedPlayers = getLeaderboards(kit);
                List<LeaderboardsEntry> entries = new ArrayList<>();
                sortedPlayers.forEach(doc -> {
                    UUID uuid = UUID.fromString(doc.getString("uuid"));
                    Document kitStatistics = (Document) doc.get("profileStatistics");
                    try {
                        Document kitDocument = (Document) kitStatistics.get(kit.getName());
                        Integer elo = (Integer) kitDocument.getOrDefault("elo", 1000);
                        entries.add(new LeaderboardsEntry(Bukkit.getOfflinePlayer(uuid).getName(), elo));
                    } catch (Exception e) {
                        entries.add(new LeaderboardsEntry(Bukkit.getOfflinePlayer(uuid).getName(), 1000));
                    }
                });
                updated.put(kit, entries);
            });
            kitLeaderboards = updated;
            sortLeaderboards();
        });
    }

    private static void sortLeaderboards() {
        kitLeaderboards = kitLeaderboards.entrySet().stream()
                .sorted((entry1, entry2) -> {
                    List<LeaderboardsEntry> list1 = entry1.getValue();
                    List<LeaderboardsEntry> list2 = entry2.getValue();
                    return Integer.compare(list2.size(), list1.size());
                })
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private static List<Document> getLeaderboards(Kit kit) {
        try {
            Document sortCriteria = new Document("profileStatistics." + kit.getName() + ".elo", -1);
            return Profile.getCollection()
                    .find()
                    .sort(sortCriteria)
                    .limit(10)
                    .into(new ArrayList<>());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }

    }

    public LeaderboardsEntry getByPlayer(Kit kit, Player player) {
        AtomicReference<LeaderboardsEntry> ent = new AtomicReference<>();
        kitLeaderboards.get(kit).forEach(entry -> {
            if (entry.getName().equals(player.getName())) {
                ent.set(entry);
            }
        });
        return ent.get();
    }

}
