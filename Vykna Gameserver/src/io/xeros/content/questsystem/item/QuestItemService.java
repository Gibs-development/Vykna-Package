package io.xeros.content.questsystem.item;

import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestProfile;
import io.xeros.content.questsystem.model.QuestState;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;

import java.util.List;
import java.util.Map;

public final class QuestItemService {
    private static final QuestItemRegistry REGISTRY = new QuestItemRegistry();

    private QuestItemService() {
    }

    public static QuestItemRegistry getRegistry() {
        return REGISTRY;
    }

    public static void registerDefaultDefinitions() {
        REGISTRY.register(QuestItemDefinition.builder("cooks_assistant", 1929)
                .undroppable(true)
                .untradeable(true)
                .reclaimPolicy(QuestItemReclaimPolicy.INFINITE)
                .stageRange(3, 3)
                .build());
    }

    public static boolean isQuestItem(Player player, int itemId) {
        return !getActiveDefinitions(player, itemId).isEmpty();
    }

    public static boolean isDroppable(Player player, int itemId) {
        for (QuestItemDefinition definition : getActiveDefinitions(player, itemId)) {
            if (definition.undroppable()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isTradable(Player player, int itemId) {
        for (QuestItemDefinition definition : getActiveDefinitions(player, itemId)) {
            if (definition.untradeable()) {
                return false;
            }
        }
        return true;
    }

    public static int reclaimMissingItems(Player player) {
        if (player == null) {
            return 0;
        }
        QuestProfile profile = player.getQuestProfile();
        profile.ensureDefaults();
        int reclaimed = 0;
        for (Map.Entry<String, QuestProgress> entry : profile.getQuests().entrySet()) {
            QuestProgress progress = entry.getValue();
            if (progress == null) {
                continue;
            }
            progress.ensureDefaults();
            List<QuestItemDefinition> definitions = REGISTRY.getDefinitionsForQuest(entry.getKey());
            for (QuestItemDefinition definition : definitions) {
                if (!isQuestItemRequired(progress, definition) || !definition.isReclaimable()) {
                    continue;
                }
                if (alreadyOwns(player, definition.itemId(), definition.amount())) {
                    continue;
                }
                if (definition.reclaimPolicy() == QuestItemReclaimPolicy.ONCE && hasReclaimed(progress, definition)) {
                    continue;
                }
                player.getItems().addItemUnderAnyCircumstance(definition.itemId(), definition.amount());
                markReclaimed(progress, definition);
                reclaimed++;
            }
        }
        return reclaimed;
    }

    private static boolean isQuestItemRequired(QuestProgress progress, QuestItemDefinition definition) {
        if (progress.getState() != QuestState.IN_PROGRESS) {
            return false;
        }
        return definition.matchesStage(progress.getStage());
    }

    private static boolean alreadyOwns(Player player, int itemId, int amount) {
        ItemAssistant items = player.getItems();
        return items.getItemCount(itemId, false) >= amount;
    }

    private static List<QuestItemDefinition> getActiveDefinitions(Player player, int itemId) {
        if (player == null) {
            return List.of();
        }
        QuestProfile profile = player.getQuestProfile();
        profile.ensureDefaults();
        List<QuestItemDefinition> candidates = REGISTRY.getDefinitions(itemId);
        if (candidates.isEmpty()) {
            return List.of();
        }
        return candidates.stream()
                .filter(definition -> {
                    QuestProgress progress = profile.getQuests().get(definition.questId());
                    if (progress == null) {
                        return false;
                    }
                    progress.ensureDefaults();
                    return isQuestItemRequired(progress, definition);
                })
                .toList();
    }

    private static String reclaimKey(QuestItemDefinition definition) {
        return "questitem." + definition.itemId() + ".reclaimed";
    }

    private static boolean hasReclaimed(QuestProgress progress, QuestItemDefinition definition) {
        Object value = progress.getVars().get(reclaimKey(definition));
        return value instanceof Boolean && (Boolean) value;
    }

    private static void markReclaimed(QuestProgress progress, QuestItemDefinition definition) {
        if (definition.reclaimPolicy() == QuestItemReclaimPolicy.INFINITE) {
            return;
        }
        progress.getVars().put(reclaimKey(definition), true);
    }
}
