package io.xeros.content.questsystem.service;

import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.model.QuestId;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestState;
import io.xeros.model.entity.player.Player;

import java.util.List;
public interface QuestService {
    QuestProgress startQuest(QuestId questId);

    QuestProgress getProgress(QuestId questId);

    QuestState getState(QuestId questId);

    void advanceObjective(QuestId questId, String objectiveId);

    void completeQuest(QuestId questId);

    void handle(Player player, QuestEvent event);

    List<String> getQuestJournalLines(Player player, String questId);
}
