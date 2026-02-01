package io.xeros.content.questsystem.service;

import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.model.QuestId;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestState;
import io.xeros.model.entity.player.Player;

import java.util.List;
public class QuestServiceStub implements QuestService {
    @Override
    public QuestProgress startQuest(QuestId questId) {
        throw new UnsupportedOperationException("QuestService stub");
    }

    @Override
    public QuestProgress getProgress(QuestId questId) {
        throw new UnsupportedOperationException("QuestService stub");
    }

    @Override
    public QuestState getState(QuestId questId) {
        throw new UnsupportedOperationException("QuestService stub");
    }

    @Override
    public void advanceObjective(QuestId questId, String objectiveId) {
        throw new UnsupportedOperationException("QuestService stub");
    }

    @Override
    public void completeQuest(QuestId questId) {
        throw new UnsupportedOperationException("QuestService stub");
    }

    @Override
    public void handle(Player player, QuestEvent event) {
        throw new UnsupportedOperationException("QuestService stub");
    }

    @Override
    public List<String> getQuestJournalLines(Player player, String questId) {
        throw new UnsupportedOperationException("QuestService stub");
    }
}
