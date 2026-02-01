package io.xeros.content.questsystem.step;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.event.QuestEventKeys;
import io.xeros.content.questsystem.event.QuestEventType;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestStep;
import io.xeros.model.entity.player.Player;

public class BringItemsStep extends QuestStepSupport implements QuestStep {
    private final String stepId;
    private final Map<Integer, Integer> requiredItems;
    private final List<String> journalText;

    public BringItemsStep(String stepId, Map<Integer, Integer> requiredItems, List<String> journalText) {
        this.stepId = stepId;
        this.requiredItems = requiredItems == null ? new HashMap<>() : new HashMap<>(requiredItems);
        this.journalText = journalText;
    }

    @Override
    public List<String> getJournalText(QuestProgress progress) {
        return journalText;
    }

    @Override
    public void onStart(Player player, QuestProgress progress) {
        setBool(progress, key(stepId, "started"), true);
    }

    @Override
    public void onEvent(Player player, QuestProgress progress, QuestEvent event) {
        if (event.getType() != QuestEventType.ITEM_ADDED) {
            return;
        }
        Object itemValue = event.get(QuestEventKeys.ITEM_ID);
        Object amountValue = event.get(QuestEventKeys.ITEM_AMOUNT);
        if (!(itemValue instanceof Number)) {
            return;
        }
        int itemId = ((Number) itemValue).intValue();
        if (!requiredItems.containsKey(itemId)) {
            return;
        }
        int amount = amountValue instanceof Number ? ((Number) amountValue).intValue() : 1;
        if (amount <= 0) {
            return;
        }
        String key = key(stepId, "item." + itemId);
        int current = getInt(progress, key);
        setInt(progress, key, current + amount);
    }

    @Override
    public boolean isComplete(Player player, QuestProgress progress) {
        for (Map.Entry<Integer, Integer> entry : requiredItems.entrySet()) {
            String key = key(stepId, "item." + entry.getKey());
            if (getInt(progress, key) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onComplete(Player player, QuestProgress progress) {
        setBool(progress, key(stepId, "complete"), true);
    }

    public Map<Integer, Integer> getRequiredItems() {
        return Collections.unmodifiableMap(requiredItems);
    }
}
