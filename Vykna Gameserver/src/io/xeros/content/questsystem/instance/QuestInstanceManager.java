package io.xeros.content.questsystem.instance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public final class QuestInstanceManager {
    private static final Map<String, QuestBossInstance> INSTANCES = new ConcurrentHashMap<>();

    private QuestInstanceManager() {
    }

    public static QuestBossInstance getOrCreate(Player player,
                                                String questId,
                                                String stepId,
                                                Boundary boundary,
                                                Position entry,
                                                Position exit,
                                                Position bossSpawn,
                                                int bossNpcId,
                                                int bossMaxHit,
                                                long timeoutMillis) {
        String key = key(player, questId, stepId);
        return INSTANCES.computeIfAbsent(key, k -> new QuestBossInstance(player, questId, stepId, boundary, entry, exit,
                bossSpawn, bossNpcId, bossMaxHit, timeoutMillis));
    }

    public static QuestBossInstance get(Player player, String questId, String stepId) {
        return INSTANCES.get(key(player, questId, stepId));
    }

    public static void remove(Player player, String questId, String stepId) {
        INSTANCES.remove(key(player, questId, stepId));
    }

    private static String key(Player player, String questId, String stepId) {
        String name = player == null ? "unknown" : player.getLoginNameLower();
        return name + ":" + questId + ":" + stepId;
    }
}
