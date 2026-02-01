package io.xeros.content.questsystem.step;

import java.util.List;

import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.event.QuestEventKeys;
import io.xeros.content.questsystem.event.QuestEventType;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestStep;
import io.xeros.model.entity.player.Player;

public class TalkToNpcStep extends QuestStepSupport implements QuestStep {
    private final String stepId;
    private final int npcId;
    private final List<String> journalText;

    public TalkToNpcStep(String stepId, int npcId, List<String> journalText) {
        this.stepId = stepId;
        this.npcId = npcId;
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
        if (event.getType() != QuestEventType.NPC_TALK) {
            return;
        }
        Object value = event.get(QuestEventKeys.NPC_ID);
        if (value instanceof Number && ((Number) value).intValue() == npcId) {
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
