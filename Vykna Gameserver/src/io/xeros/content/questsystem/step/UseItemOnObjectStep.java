package io.xeros.content.questsystem.step;

import java.util.List;

import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.event.QuestEventKeys;
import io.xeros.content.questsystem.event.QuestEventType;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestStep;
import io.xeros.model.entity.player.Player;

public class UseItemOnObjectStep extends QuestStepSupport implements QuestStep {
    private final String stepId;
    private final int itemId;
    private final int objectId;
    private final List<String> journalText;

    public UseItemOnObjectStep(String stepId, int itemId, int objectId, List<String> journalText) {
        this.stepId = stepId;
        this.itemId = itemId;
        this.objectId = objectId;
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
        if (event.getType() != QuestEventType.ITEM_ON_OBJECT) {
            return;
        }
        Object itemValue = event.get(QuestEventKeys.ITEM_ID);
        Object objectValue = event.get(QuestEventKeys.OBJECT_ID);
        if (!(itemValue instanceof Number) || !(objectValue instanceof Number)) {
            return;
        }
        if (((Number) itemValue).intValue() == itemId && ((Number) objectValue).intValue() == objectId) {
            setBool(progress, key(stepId, "complete"), true);
        }
    }

    @Override
    public boolean isComplete(Player player, QuestProgress progress) {
        return getBool(progress, key(stepId, "complete"));
    }

    @Override
    public void onComplete(Player player, QuestProgress progress) {
        setBool(progress, key(stepId, "complete"), true);
    }
}
